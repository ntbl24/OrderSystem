package com.abug.order.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abug.common.exception.AppException;
import com.abug.common.exception.ErrorCode;
import com.abug.order.application.port.out.OrderRepository;
import com.abug.order.domain.Order;
import com.abug.order.domain.valueobject.OrderId;
import com.abug.payment.domain.PaymentId;
import com.abug.user.domain.UserId;

@ExtendWith(MockitoExtension.class)
class PayableOrderQueryServiceTest {
    private static final OrderId ORDER_ID = OrderId.of("order");
    private static final UserId BUYER_ID = UserId.of("00000000-0000-0000-0000-000000000001");

    @Mock
    OrderRepository orders;
    @Mock
    Order order;
    @InjectMocks
    PayableOrderService service;

    @Test
    void lockForPayment_whenPayable_returnsSnapshotUnderLock() {
        var expiresAt = Instant.parse("2026-10-22T05:00:00Z");
        when(orders.lockById(ORDER_ID)).thenReturn(Optional.of(order));
        when(order.getId()).thenReturn(ORDER_ID);
        when(order.getUserId()).thenReturn(BUYER_ID);
        when(order.getTotal()).thenReturn(new BigDecimal("130000"));
        when(order.getExpiresAt()).thenReturn(expiresAt);

        var result = service.lockForPayment(ORDER_ID, BUYER_ID);

        assertThat(result.id()).isEqualTo(ORDER_ID);
        assertThat(result.buyerId()).isEqualTo(BUYER_ID);
        assertThat(result.amount()).isEqualByComparingTo("130000");
        assertThat(result.currency()).isEqualTo("VND");
        assertThat(result.expiresAt()).isEqualTo(expiresAt);
        verify(orders).lockById(ORDER_ID);
    }

    @Test
    void lockForPayment_whenDifferentBuyer_hidesOrder() {
        when(orders.lockById(ORDER_ID)).thenReturn(Optional.of(order));
        when(order.getUserId()).thenReturn(BUYER_ID);

        assertThatThrownBy(() -> service.lockForPayment(ORDER_ID,
                UserId.of("00000000-0000-0000-0000-000000000002")))
                .isInstanceOfSatisfying(AppException.class,
                        exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ORDER_NOT_FOUND));
    }

    @Test
    void lockForPayment_whenOrderCannotBePaid_rejectsPayment() {
        when(orders.lockById(ORDER_ID)).thenReturn(Optional.of(order));
        when(order.getUserId()).thenReturn(BUYER_ID);
        when(order.expire(any(Instant.class))).thenReturn(true);

        assertThatThrownBy(() -> service.lockForPayment(ORDER_ID, BUYER_ID))
                .isInstanceOf(AppException.class).hasMessage("Order is not payable");
    }

    @Test
    void lockForPayment_whenMissing_hidesOrder() {
        when(orders.lockById(ORDER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.lockForPayment(ORDER_ID, BUYER_ID))
                .isInstanceOfSatisfying(AppException.class,
                        exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ORDER_NOT_FOUND));
    }

    @Test
    void lockForCallback_whenOrderPaid_stillLocksOrder() {
        when(orders.lockById(ORDER_ID)).thenReturn(Optional.of(order));

        service.lockForCallback(ORDER_ID);

        verify(orders).lockById(ORDER_ID);
    }

    @Test
    void paymentMethods_requireCallerTransaction() throws Exception {
        assertThat(PayableOrderService.class.getMethod("lockForPayment", OrderId.class, UserId.class)
                .getAnnotation(Transactional.class).propagation()).isEqualTo(Propagation.MANDATORY);
        assertThat(PayableOrderService.class.getMethod("lockForCallback", OrderId.class)
                .getAnnotation(Transactional.class).propagation()).isEqualTo(Propagation.MANDATORY);
    }
}
