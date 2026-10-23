package io.order.backend.order.application.usecase;

import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.order.application.port.out.OrderRepository;
import io.order.backend.order.domain.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayableOrderServiceTest {
    @Mock OrderRepository orders;
    @InjectMocks PayableOrderService service;
    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId("order");
        order.setUserId("buyer");
        order.setOrderStatus("UNPAID");
        order.setSubtotal(new BigDecimal("100000"));
        order.setShippingCost(new BigDecimal("30000"));
        order.setExpiresAt(Instant.now().plusSeconds(900));
        when(orders.lockById("order")).thenReturn(Optional.of(order));
    }

    @Test
    void lockForPayment_whenPayable_returnsSnapshotUnderLock() {
        var result = service.lockForPayment("order", "buyer");
        assertThat(result.id()).isEqualTo("order");
        assertThat(result.buyerId()).isEqualTo("buyer");
        assertThat(result.amount()).isEqualByComparingTo("130000");
        assertThat(result.currency()).isEqualTo("VND");
        assertThat(result.expiresAt()).isEqualTo(order.getExpiresAt());
        verify(orders).lockById("order");
    }

    @Test
    void lockForPayment_whenDifferentBuyer_hidesOrder() {
        assertThatThrownBy(() -> service.lockForPayment("order", "other"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void lockForPayment_whenPaid_rejectsPayment() {
        order.setOrderStatus("PAID");
        assertThatThrownBy(() -> service.lockForPayment("order", "buyer"))
                .isInstanceOf(IllegalStateException.class).hasMessage("Order is not payable");
    }

    @Test
    void lockForPayment_whenExpired_rejectsPayment() {
        order.setExpiresAt(Instant.now().minusSeconds(1));
        assertThatThrownBy(() -> service.lockForPayment("order", "buyer"))
                .isInstanceOf(IllegalStateException.class).hasMessage("Order is not payable");
    }

    @Test
    void lockForPayment_whenMissing_hidesOrder() {
        when(orders.lockById("order")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.lockForPayment("order", "buyer"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void lockForCallback_whenPresent_locksWithoutPayabilityChecks() {
        order.setOrderStatus("PAID");
        service.lockForCallback("order");
        verify(orders).lockById("order");
    }

    @Test
    void paymentMethods_requireCallerTransaction() throws Exception {
        service.lockForCallback("order");
        assertThat(PayableOrderService.class.getMethod("lockForPayment", String.class, String.class)
                .getAnnotation(Transactional.class).propagation()).isEqualTo(Propagation.MANDATORY);
        assertThat(PayableOrderService.class.getMethod("lockForCallback", String.class)
                .getAnnotation(Transactional.class).propagation()).isEqualTo(Propagation.MANDATORY);
    }
}
