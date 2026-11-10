package io.order.backend.order.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.order.backend.order.application.port.in.PaymentEventUseCase.PaymentEvent;
import io.order.backend.order.application.port.out.InventoryPort;
import io.order.backend.order.application.port.out.OrderRepository;
import io.order.backend.order.domain.Order;
import io.order.backend.order.domain.OrderId;
import io.order.backend.order.domain.OrderItem;
import io.order.backend.payment.domain.PaymentId;
import io.order.backend.product.domain.ProductId;

@ExtendWith(MockitoExtension.class)
class PaymentEventServiceTest {
    private static final OrderId ORDER_ID = OrderId.of("order");
    private static final PaymentId PAYMENT_ID = PaymentId.of("payment");
    private static final ProductId PRODUCT_ID = ProductId.of("product");
    private static final PaymentEvent EVENT =
            new PaymentEvent(ORDER_ID, PAYMENT_ID, new BigDecimal("130000"), "VND");

    @Mock
    OrderRepository orders;
    @Mock
    InventoryPort inventory;
    @Mock
    Order order;
    @InjectMocks
    PaymentEventService service;

    @BeforeEach
    void setUp() {
        when(orders.lockById(ORDER_ID)).thenReturn(Optional.of(order));
    }

    @Test
    void paymentSucceeded_whenDeliveredTwice_consumesReservationOnce() {
        when(order.acceptPayment(any(), any(), any(), any())).thenReturn(true, false);
        when(order.getOrderStatus()).thenReturn("PAID");
        when(order.requiresReservationTransition("CONSUMED")).thenReturn(true);
        when(order.getItems()).thenReturn(List.of(new OrderItem(PRODUCT_ID, "Product", "SKU", 1,
                new BigDecimal("100000"))));

        service.paymentSucceeded(EVENT);
        service.paymentSucceeded(EVENT);

        verify(inventory).settle(List.of(new InventoryPort.Line(PRODUCT_ID, 1)), "CONSUMED");
        verify(order).transitionReservation("CONSUMED");
        verify(orders, times(2)).save(order);
    }

    @Test
    void paymentSucceeded_whenOrderExpired_releasesReservationForReview() {
        when(order.acceptPayment(any(), any(), any(), any())).thenReturn(false);
        when(order.getOrderStatus()).thenReturn("PAYMENT_REVIEW");
        when(order.getReservationStatus()).thenReturn("HELD", "RELEASED");
        when(order.requiresReservationTransition("RELEASED")).thenReturn(true, false);
        when(order.getItems()).thenReturn(List.of(new OrderItem(PRODUCT_ID, "Product", "SKU", 1,
                new BigDecimal("100000"))));

        service.paymentSucceeded(EVENT);
        service.paymentSucceeded(EVENT);

        verify(inventory).settle(List.of(new InventoryPort.Line(PRODUCT_ID, 1)), "RELEASED");
        verify(order).transitionReservation("RELEASED");
        verify(orders, times(2)).save(order);
    }

    @Test
    void paymentRefunded_whenDeliveredTwice_returnsConsumedStockOnce() {
        when(order.acceptPayment(any(), any(), any(), any())).thenReturn(true, false);
        when(order.acceptRefund(any(), any(), any())).thenReturn(true, false);
        when(order.getReservationStatus()).thenReturn("CONSUMED");
        when(order.requiresReservationTransition("CONSUMED")).thenReturn(true);
        when(order.requiresReservationTransition("RETURNED")).thenReturn(true);
        when(order.getItems()).thenReturn(List.of(new OrderItem(PRODUCT_ID, "Product", "SKU", 1,
                new BigDecimal("100000"))));

        service.paymentSucceeded(EVENT);
        service.paymentRefunded(EVENT);
        service.paymentRefunded(EVENT);

        verify(inventory).settle(List.of(new InventoryPort.Line(PRODUCT_ID, 1)), "CONSUMED");
        verify(inventory).settle(List.of(new InventoryPort.Line(PRODUCT_ID, 1)), "RETURNED");
        verify(order).transitionReservation("CONSUMED");
        verify(order).transitionReservation("RETURNED");
        verify(orders, times(3)).save(order);
    }
}
