package io.order.backend.order.application.usecase;

import io.order.backend.order.application.port.in.PaymentEventUseCase.PaymentEvent;
import io.order.backend.order.application.port.out.InventoryPort;
import io.order.backend.order.application.port.out.OrderRepository;
import io.order.backend.order.domain.Order;
import io.order.backend.order.domain.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentEventServiceTest {
    @Mock OrderRepository orders;
    @Mock InventoryPort inventory;
    @InjectMocks PaymentEventService service;
    Order order;
    PaymentEvent event;

    @BeforeEach
    void pendingOrder() {
        var item = new OrderItem();
        item.setProductId("product");
        item.setQuantity(2);
        order = Order.place("buyer", "seller", List.of(item), new BigDecimal("100000"),
                "address", null, Instant.now(), LocalDateTime.now());
        event = new PaymentEvent(order.getId(), "payment", new BigDecimal("130000"), "VND");
        when(orders.lockById(order.getId())).thenReturn(Optional.of(order));
    }

    @Test
    void paymentSucceeded_duplicateDelivery_consumesReservationOnce() {
        service.paymentSucceeded(event);
        service.paymentSucceeded(event);

        assertThat(order.getOrderStatus()).isEqualTo("PAID");
        assertThat(order.getPaymentId()).isEqualTo("payment");
        assertThat(order.getReservationStatus()).isEqualTo("CONSUMED");
        verify(inventory).settle(List.of(new InventoryPort.Line("product", 2)), "CONSUMED");
        verify(orders, times(2)).save(order);
    }

    @Test
    void paymentSucceeded_expiredOrder_releasesReservationForReview() {
        order.setExpiresAt(Instant.now().minusSeconds(1));

        service.paymentSucceeded(event);
        service.paymentSucceeded(event);

        assertThat(order.getOrderStatus()).isEqualTo("PAYMENT_REVIEW");
        assertThat(order.getReviewPaymentId()).isEqualTo("payment");
        assertThat(order.getReservationStatus()).isEqualTo("RELEASED");
        verify(inventory).settle(List.of(new InventoryPort.Line("product", 2)), "RELEASED");
        verifyNoMoreInteractions(inventory);
    }

    @Test
    void paymentRefunded_duplicateDelivery_returnsConsumedStockOnce() {
        service.paymentSucceeded(event);
        service.paymentRefunded(event);
        service.paymentRefunded(event);

        assertThat(order.getOrderStatus()).isEqualTo("REFUNDED");
        assertThat(order.getReservationStatus()).isEqualTo("RETURNED");
        verify(inventory).settle(List.of(new InventoryPort.Line("product", 2)), "CONSUMED");
        verify(inventory).settle(List.of(new InventoryPort.Line("product", 2)), "RETURNED");
        verifyNoMoreInteractions(inventory);
    }
}
