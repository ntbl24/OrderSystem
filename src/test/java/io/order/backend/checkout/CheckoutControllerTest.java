package io.order.backend.checkout;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.order.backend.checkout.application.port.in.CheckoutUseCase;
import io.order.backend.checkout.infrastructure.web.controller.CheckoutController;
import io.order.backend.order.domain.Order;
import io.order.backend.order.domain.OrderId;
import io.order.backend.order.domain.OrderNumber;
import io.order.backend.order.domain.PaymentStatus;
import io.order.backend.order.domain.OrderStatus;
import io.order.backend.order.domain.ReservationStatus;
import io.order.backend.user.domain.UserId;

@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {
    @Mock
    CheckoutUseCase checkout;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new CheckoutController(checkout)).build();
    }

    @Test
    void checkout_whenValid_mapsRequestAndReturnsCreatedOrder() throws Exception {
        var id = OrderId.of("checkout-order");
        var order = new Order(id, UserId.of("00000000-0000-0000-0000-000000000001"),
                OrderNumber.fromOrderId(id), new BigDecimal("100000"), new BigDecimal("30000"),
                "address", "VNPAY", PaymentStatus.UNPAID, OrderStatus.UNPAID, LocalDateTime.now(), null, "notes",
                "seller", Instant.now().plusSeconds(900), null, null, false, ReservationStatus.HELD, 1L);
        when(checkout.checkout(eq("request-key"), any())).thenReturn(order);

        mvc.perform(post("/api/v1/checkouts").header("Idempotency-Key", "request-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"items":[{"productId":"product-id","quantity":1}],
                         "shippingAddress":"address","notes":"notes"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(order.getId().toString()))
                .andExpect(jsonPath("$.data.orderNumber").value(order.getOrderNumber().toString()))
                .andExpect(jsonPath("$.data.total").value(130000))
                .andExpect(jsonPath("$.data.userId").value("00000000-0000-0000-0000-000000000001"));

        verify(checkout).checkout(eq("request-key"), argThat(command ->
                command.items().getFirst().productId().equals("product-id")
                        && command.items().getFirst().quantity() == 1
                        && command.shippingAddress().equals("address")
                        && command.notes().equals("notes")));
    }

    @Test
    void checkout_whenQuantityInvalid_returnsBadRequestWithoutCallingUseCase() throws Exception {
        mvc.perform(post("/api/v1/checkouts").header("Idempotency-Key", "request-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"items":[{"productId":"p","quantity":0}],"shippingAddress":"address"}
                        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(checkout);
    }
}
