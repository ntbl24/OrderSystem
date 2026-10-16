package io.order.backend.order.domain;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;

class OrderLifecycleTest {
    private Order unpaid() {
        var o = new Order();
        o.setOrderStatus("UNPAID");
        o.setPaymentStatus("UNPAID");
        o.setSubtotal(new BigDecimal("100000"));
        o.setShippingCost(new BigDecimal("30000"));
        o.setExpiresAt(Instant.now().plusSeconds(900));
        return o;
    }

    @Test
    void payment_whenValid_consumesOnce() {
        var o = unpaid();
        assertThat(o.acceptPayment("payment", new BigDecimal("130000"), "VND", Instant.now())).isTrue();
        assertThat(o.acceptPayment("payment", new BigDecimal("130000"), "VND", Instant.now())).isFalse();
        assertThat(o.getOrderStatus()).isEqualTo("PAID");
        assertThat(o.expire(Instant.now().plusSeconds(1000))).isFalse();
        assertThatThrownBy(o::cancel).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void payment_afterCancellation_requiresReview() {
        var o = unpaid();
        o.cancel();
        assertThat(o.acceptPayment("late", new BigDecimal("130000"), "VND", Instant.now())).isFalse();
        assertThat(o.getOrderStatus()).isEqualTo("PAYMENT_REVIEW");
    }

    @Test
    void payment_wrongAmount_requiresReview() {
        var o = unpaid();
        assertThat(o.acceptPayment("bad", BigDecimal.ONE, "VND", Instant.now())).isFalse();
        assertThat(o.getOrderStatus()).isEqualTo("PAYMENT_REVIEW");
    }

    @Test
    void payment_secondCharge_requiresReviewWithoutReplacingFirst() {
        var o = unpaid();
        o.acceptPayment("first", new BigDecimal("130000"), "VND", Instant.now());
        assertThat(o.acceptPayment("second", new BigDecimal("130000"), "VND", Instant.now())).isFalse();
        assertThat(o.getPaymentId()).isEqualTo("first");
        assertThat(o.getOrderStatus()).isEqualTo("PAYMENT_REVIEW");
    }

    @Test
    void refund_whenRepeated_returnsOnce() {
        var o = unpaid();
        o.acceptPayment("first", new BigDecimal("130000"), "VND", Instant.now());
        assertThat(o.acceptRefund("first", new BigDecimal("130000"), "VND")).isTrue();
        assertThat(o.acceptRefund("first", new BigDecimal("130000"), "VND")).isFalse();
    }

    @Test
    void inventory_enforcesReservedStock() {
        var p = new io.order.backend.product.domain.Product();
        p.setStock(3);
        p.reserve(2);
        assertThatThrownBy(() -> p.reserve(2)).isInstanceOf(IllegalStateException.class);
        p.consume(2);
        assertThat(p.getStock()).isEqualTo(1);
        assertThat(p.getReserved()).isZero();
        p.restock(2);
        assertThat(p.getStock()).isEqualTo(3);
    }
}
