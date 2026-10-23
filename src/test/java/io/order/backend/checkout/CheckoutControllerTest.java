package io.order.backend.checkout;

import io.order.backend.checkout.application.port.in.CheckoutUseCase;
import io.order.backend.checkout.infrastructure.web.controller.CheckoutController;
import io.order.backend.order.application.port.in.OrderResult;
import io.order.backend.order.application.port.in.OrderItemData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.math.BigDecimal;
import java.util.List;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {
    @Mock CheckoutUseCase checkout;
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new CheckoutController(checkout)).build();
    }

    @Test
    void checkout_validRequest_mapsCommandAndPreservesCreatedResponse() throws Exception {
        var result = new OrderResult();
        result.setId("order-id");
        result.setUserId("buyer");
        result.setOrderNumber("ORD-order-id");
        result.setTotal(new BigDecimal("130000"));
        var item = new OrderItemData();
        item.setProductId("product-id");
        item.setProductName("Server snapshot");
        item.setQuantity(1);
        result.setItems(List.of(item));
        when(checkout.checkout(eq("request-key"), any())).thenReturn(result);

        mvc.perform(post("/api/v1/checkouts").header("Idempotency-Key", "request-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"items\":[{\"productId\":\"product-id\",\"quantity\":1}],\"shippingAddress\":\"address\",\"notes\":\"notes\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("order-id"))
                .andExpect(jsonPath("$.data.orderNumber").value("ORD-order-id"))
                .andExpect(jsonPath("$.data.total").value(130000))
                .andExpect(jsonPath("$.data.items[0].productName").value("Server snapshot"));
        verify(checkout).checkout(eq("request-key"), argThat(command ->
                command.items().getFirst().productId().equals("product-id")
                && command.items().getFirst().quantity() == 1
                && command.shippingAddress().equals("address") && command.notes().equals("notes")));
    }

    @Test
    void checkout_invalidQuantity_rejectsBeforeCallingUseCase() throws Exception {
        mvc.perform(post("/api/v1/checkouts").header("Idempotency-Key", "request-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"items\":[{\"productId\":\"p\",\"quantity\":0}],\"shippingAddress\":\"address\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(checkout);
    }
}
