package com.abug.order;

import com.abug.order.application.port.in.OrderUseCase;
import com.abug.order.domain.Order;
import com.abug.order.domain.OrderId;
import com.abug.order.domain.OrderNumber;
import com.abug.order.infrastructure.web.controller.OrderController;
import com.abug.user.domain.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {
    private static final String BUYER_ID = "00000000-0000-0000-0000-000000000001";

    @Mock OrderUseCase orders;
    MockMvc mvc;
    Order order;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new OrderController(orders)).build();
        OrderId id = OrderId.of("order-id");
        order = new Order(id, UserId.of(BUYER_ID), OrderNumber.fromOrderId(id),
                new BigDecimal("100000"), new BigDecimal("30000"), "address", "VNPAY",
                "UNPAID", "UNPAID", LocalDateTime.of(2026, 10, 22, 10, 0), null, null,
                "seller", Instant.parse("2026-10-22T05:00:00Z"), null, null, false, "HELD", 1L);
    }

    @Test
    void getOrderById_returnsIdentifiersMoneyAndStatusFields() throws Exception {
        when(orders.getOrderById(order.getId())).thenReturn(order);

        mvc.perform(get("/api/v1/orders/{id}", order.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(order.getId().toString()))
                .andExpect(jsonPath("$.data.userId").value(BUYER_ID))
                .andExpect(jsonPath("$.data.sellerId").value("seller"))
                .andExpect(jsonPath("$.data.orderNumber").value(order.getOrderNumber().toString()))
                .andExpect(jsonPath("$.data.subtotal").value(100000))
                .andExpect(jsonPath("$.data.shippingCost").value(30000))
                .andExpect(jsonPath("$.data.total").value(130000))
                .andExpect(jsonPath("$.data.paymentMethod").value("VNPAY"))
                .andExpect(jsonPath("$.data.paymentStatus").value("UNPAID"))
                .andExpect(jsonPath("$.data.orderStatus").value("UNPAID"));
    }

    @Test
    void orderNumberUserListAndCancel_preserveRoutesAndResponseMapping() throws Exception {
        when(orders.getOrderByOrderNumber(order.getOrderNumber())).thenReturn(order);
        when(orders.getOrdersByUserId(order.getUserId())).thenReturn(List.of(order));
        when(orders.cancel(order.getId())).thenAnswer(call -> {
            order.cancel();
            return order;
        });

        mvc.perform(get("/api/v1/orders/number/{number}", order.getOrderNumber().toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(order.getId().toString()));
        mvc.perform(get("/api/v1/orders/user/{userId}", BUYER_ID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(order.getId().toString()));
        mvc.perform(post("/api/v1/orders/{id}/cancel", order.getId().toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.orderStatus").value("CANCELLED"));
        verify(orders).cancel(order.getId());
    }
}
