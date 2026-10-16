package io.order.backend.payment;

import io.order.backend.payment.api.PaymentController;
import io.order.backend.payment.application.port.PaymentUseCase;
import io.order.backend.payment.infrastructure.provider.VnPayCallbacks;
import io.order.backend.payment.infrastructure.web.VnPayCallbackController;
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
    @Mock PaymentUseCase payments;
    @Mock VnPayCallbacks callbacks;
    MockMvc mvc;

    @BeforeEach void setup() {
        mvc = MockMvcBuilders.standaloneSetup(new PaymentController(payments),
                new VnPayCallbackController(callbacks)).build();
    }

    @Test void create_allAliases_preserveCreatedAndUseCaseArguments() throws Exception {
        for (String path : List.of("", "/attempts", "/process")) {
            mvc.perform(post("/api/v1/payments" + path).header("Idempotency-Key", "key")
                    .contentType(MediaType.APPLICATION_JSON).content("{\"orderId\":\"order\"}"))
                    .andExpect(status().isCreated());
        }
        verify(payments, times(3)).create(eq("order"), eq("key"), anyString());
    }

    @Test void refunds_bothAliases_preserveAccepted() throws Exception {
        for (String suffix : List.of("/refunds", "/refund")) {
            mvc.perform(post("/api/v1/payments/payment" + suffix).header("Idempotency-Key", "key")
                    .contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\"requested\"}"))
                    .andExpect(status().isAccepted());
        }
        verify(payments, times(2)).refund("payment", "key", "requested");
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
        when(callbacks.returnView(anyMap())).thenReturn(new PaymentUseCase.ReturnView(false, "INVALID", null));
        for (String path : List.of("/vnpay/return", "/callback/vnpay")) {
            mvc.perform(get("/api/v1/payments" + path)).andExpect(status().isOk());
        }
        verify(callbacks, times(2)).returnView(anyMap());
        verifyNoInteractions(payments);
    }
}
