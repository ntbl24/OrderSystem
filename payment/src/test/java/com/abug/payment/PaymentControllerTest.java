package com.abug.payment;

import com.abug.payment.infrastructure.web.controller.PaymentController;
import com.abug.payment.application.port.in.*;
import com.abug.payment.application.port.in.PaymentResult;
import java.math.BigDecimal;
import com.abug.payment.infrastructure.provider.VnPayCallbacks;
import com.abug.payment.infrastructure.web.controller.VnPayCallbackController;
import com.abug.payment.domain.PaymentId;
import com.abug.order.domain.OrderId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {
    @Mock CreatePaymentUsecase creates;
    @Mock PaymentQueryUseCase payments;
    @Mock RefundPaymentUseCase refunds;
    @Mock VnPayCallbacks callbacks;
    MockMvc mvc;

    @BeforeEach void setup() {
        mvc = MockMvcBuilders.standaloneSetup(new PaymentController(creates, payments, refunds),
                new VnPayCallbackController(callbacks)).build();
    }

    private static PaymentResult result() {
        return PaymentResult.from(PaymentTestFixtures.pending(BigDecimal.TEN), "https://provider.test/pay");
    }

    @Test void create_allAliases_preserveCreatedAndUseCaseArguments() throws Exception {
        when(creates.create(any(CreatePaymentCommand.class))).thenReturn(result());
        for (String path : List.of("", "/attempts", "/process")) {
            mvc.perform(post("/api/v1/payments" + path).header("Idempotency-Key", "key")
                    .contentType(MediaType.APPLICATION_JSON).content("{\"orderId\":\"order\"}"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(PaymentId.of("payment").toString()))
                    .andExpect(jsonPath("$.data.orderId").value(OrderId.of("order").toString()))
                    .andExpect(jsonPath("$.data.amount").value(10))
                    .andExpect(jsonPath("$.data.currency").value("VND"))
                    .andExpect(jsonPath("$.data.status").value("PENDING"))
                    .andExpect(jsonPath("$.data.refundStatus").value("NONE"))
                    .andExpect(jsonPath("$.data.paymentUrl").value("https://provider.test/pay"));
        }
        verify(creates, times(3)).create(argThat(c -> c.orderId().equals(com.abug.order.domain.OrderId.of("order")) && c.key().equals("key")));
    }

    @Test void refunds_bothAliases_preserveAccepted() throws Exception {
        when(refunds.refund(PaymentId.of("payment"), "key", "requested")).thenReturn(result());
        for (String suffix : List.of("/refunds", "/refund")) {
            mvc.perform(post("/api/v1/payments/payment" + suffix).header("Idempotency-Key", "key")
                    .contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\"requested\"}"))
                    .andExpect(status().isAccepted());
        }
        verify(refunds, times(2)).refund(PaymentId.of("payment"), "key", "requested");
    }

    @Test void get_mapsApplicationResultToExistingJson() throws Exception {
        when(payments.get(PaymentId.of("payment"))).thenReturn(result());
        mvc.perform(get("/api/v1/payments/payment"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(PaymentId.of("payment").toString()))
                .andExpect(jsonPath("$.data.amount").value(10));
    }

    @Test void create_blankOrder_rejectedBeforeUseCase() throws Exception {
        mvc.perform(post("/api/v1/payments").header("Idempotency-Key", "key")
                .contentType(MediaType.APPLICATION_JSON).content("{\"orderId\":\"\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(payments);
    }

    @Test void refund_protocolDelimiter_rejectedBeforeUseCase() throws Exception {
        mvc.perform(post("/api/v1/payments/payment/refunds").header("Idempotency-Key", "key")
                .contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\"invalid|reason\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(payments);
    }

    @Test void removed_mutations_preserveGone() throws Exception {
        mvc.perform(put("/api/v1/payments/payment")).andExpect(status().isGone());
        mvc.perform(patch("/api/v1/payments/payment")).andExpect(status().isGone());
        mvc.perform(delete("/api/v1/payments/payment")).andExpect(status().isGone());
        verifyNoInteractions(payments);
    }

    @Test void ipn_preservesProtocolJsonAndDuplicateValues() throws Exception {
        when(callbacks.ipn(anyMap())).thenReturn(new VnPayCallbacks.Acknowledgment("97", "Duplicate fields"));
        mvc.perform(get("/api/v1/payments/vnpay/ipn").param("vnp_TxnRef", "one", "two"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.RspCode").value("97"))
                .andExpect(jsonPath("$.Message").value("Duplicate fields"));
        verify(callbacks).ipn(argThat(fields -> fields.get("vnp_TxnRef").equals(List.of("one", "two"))));
        verifyNoInteractions(payments);
    }

    @Test void return_bothAliases_delegateWithoutSettlement() throws Exception {
        when(callbacks.returnView(anyMap())).thenReturn(new PaymentCallbackUseCase.ReturnView(false, "INVALID", null));
        for (String path : List.of("/vnpay/return", "/callback/vnpay")) {
            mvc.perform(get("/api/v1/payments" + path)).andExpect(status().isOk());
        }
        verify(callbacks, times(2)).returnView(anyMap());
        verifyNoInteractions(payments);
    }
}
