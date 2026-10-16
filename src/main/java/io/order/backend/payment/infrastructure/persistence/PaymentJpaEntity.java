package io.order.backend.payment.infrastructure.persistence;

import io.order.backend.payment.domain.Payment;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment_attempts")
public class PaymentJpaEntity {
    @Id
    String id;
    @Version
    Long version;
    @Column(name = "order_id", nullable = false)
    String orderId;
    @Column(name = "buyer_id", nullable = false)
    String buyerId;
    @Column(nullable = false, precision = 19, scale = 0)
    BigDecimal amount;
    @Column(nullable = false, length = 3)
    String currency;
    @Column(name = "transaction_ref", nullable = false, unique = true, length = 32)
    String transactionRef;
    @Column(name = "created_at", nullable = false)
    Instant createdAt;
    @Column(name = "expires_at", nullable = false)
    Instant expiresAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    Payment.Status status;
    @Column(name = "provider_transaction_no")
    String providerTransactionNo;
    @Enumerated(EnumType.STRING)
    @Column(name = "refund_status", nullable = false, length = 24)
    Payment.RefundStatus refundStatus;
    @Column(name = "refund_request_id", unique = true, length = 32)
    String refundRequestId;
    @Column(name = "refund_actor")
    String refundActor;
    @Column(name = "refund_reason", length = 255)
    String refundReason;
    @Column(name = "refund_created_at")
    Instant refundCreatedAt;
    @Column(name = "next_attempt", nullable = false)
    Instant nextAttempt;
    @Column(name = "lease_until")
    Instant leaseUntil;
    @Column(name = "lease_token")
    String leaseToken;
    @Column(nullable = false)
    int attempts;
    @Column(name = "last_outcome", length = 64)
    String lastOutcome;

    protected PaymentJpaEntity() {
    }

    static PaymentJpaEntity create(Payment p) {
        var e = new PaymentJpaEntity();
        e.id = p.id();
        e.nextAttempt = p.createdAt().plusSeconds(60);
        e.apply(p);
        return e;
    }

    void apply(Payment p) {
        orderId = p.orderId();
        buyerId = p.buyerId();
        amount = p.amount();
        currency = p.currency();
        transactionRef = p.transactionRef();
        createdAt = p.createdAt();
        expiresAt = p.expiresAt();
        status = p.status();
        providerTransactionNo = p.providerTransactionNo();
        if (refundStatus != p.refundStatus() && p.refundStatus() == Payment.RefundStatus.REQUESTED) {
            attempts = 0;
            nextAttempt = p.refundCreatedAt();
        }
        refundStatus = p.refundStatus();
        refundRequestId = p.refundRequestId();
        refundActor = p.refundActor();
        refundReason = p.refundReason();
        refundCreatedAt = p.refundCreatedAt();
    }

    Payment domain() {
        return new Payment(id, orderId, buyerId, amount, currency, transactionRef, createdAt, expiresAt, status,
                providerTransactionNo, refundStatus, refundRequestId, refundActor, refundReason, refundCreatedAt);
    }
}
