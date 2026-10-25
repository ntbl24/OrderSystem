package io.order.backend.payment;

import io.order.backend.order.domain.OrderId;
import io.order.backend.payment.domain.Payment;
import io.order.backend.payment.domain.PaymentId;
import io.order.backend.payment.domain.TransactionRef;
import io.order.backend.user.domain.UserId;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class PaymentValueObjectTest {

    @Test
    void paymentId_generate_shouldProduceValidUniqueId() {
        PaymentId id1 = PaymentId.generate();
        PaymentId id2 = PaymentId.generate();

        assertThat(id1).isNotNull();
        assertThat(id1.value()).isNotNull();
        assertThat(id1).isNotEqualTo(id2);
        assertThat(id1.toString()).isEqualTo(id1.value().toString());
    }

    @Test
    void paymentId_of_withUuidString_shouldParseCorrectly() {
        UUID uuid = UUID.randomUUID();
        PaymentId id = PaymentId.of(uuid.toString());

        assertThat(id.value()).isEqualTo(uuid);
    }

    @Test
    void paymentId_of_withNonUuidString_shouldDeriveDeterministicUuid() {
        PaymentId id1 = PaymentId.of("payment-ref-1");
        PaymentId id2 = PaymentId.of("payment-ref-1");

        assertThat(id1.value()).isNotNull();
        assertThat(id1).isEqualTo(id2);
    }

    @Test
    void paymentId_null_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> new PaymentId(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void transactionRef_whenValid_shouldHoldValue() {
        TransactionRef ref = TransactionRef.of("TXN-123456");
        assertThat(ref.value()).isEqualTo("TXN-123456");
        assertThat(ref.toString()).isEqualTo("TXN-123456");
    }

    @Test
    void transactionRef_whenNull_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> new TransactionRef(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void payment_withValueObjectAccessors_shouldExposeValuesCorrectly() {
        PaymentId paymentId = PaymentId.generate();
        OrderId orderId = OrderId.generate();
        UserId buyerId = UserId.generate();
        TransactionRef ref = TransactionRef.of("TXN-TEST");

        Payment payment = new Payment(
                paymentId.value().toString(),
                orderId.value().toString(),
                buyerId.value().toString(),
                new BigDecimal("50000"),
                "VND",
                ref.value(),
                Instant.now(),
                Instant.now().plusSeconds(900),
                Payment.Status.PENDING,
                null,
                Payment.RefundStatus.NONE,
                null,
                null,
                null,
                null
        );

        assertThat(payment.paymentId()).isEqualTo(paymentId);
        assertThat(payment.orderIdentifier()).isEqualTo(orderId);
        assertThat(payment.buyerIdentifier()).isEqualTo(buyerId);
        assertThat(payment.transactionRefVo()).isEqualTo(ref);
    }
}
