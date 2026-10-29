package io.order.backend.payment.infrastructure.persistence;

import io.order.backend.payment.domain.Payment;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

@lombok.Getter
@Entity
@Table(name = "payment_attempts")
public class PaymentJpaEntity {
    @Id
    private UUID id;
    @Version
    private Long version;
    @Column(name = "order_id", nullable = false)
    private UUID orderId;
    @Column(name = "buyer_id", nullable = false)
    private UUID buyerId;
    @Column(nullable = false, precision = 19, scale = 0)
    private BigDecimal amount;
    @Column(nullable = false, length = 3)
    private String currency;
    @Column(name = "transaction_ref", nullable = false, unique = true, length = 32)
    private String transactionRef;
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Instant createdAt;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private Payment.Status status;
    @Column(name = "provider_transaction_no")
    private String providerTransactionNo;
    @Enumerated(EnumType.STRING)
    @Column(name = "refund_status", nullable = false, length = 24)
    private Payment.RefundStatus refundStatus;
    @Column(name = "refund_request_id", unique = true, length = 32)
    private String refundRequestId;
    @Column(name = "refund_actor")
    private String refundActor;
    @Column(name = "refund_reason", length = 255)
    private String refundReason;
    @Column(name = "refund_created_at")
    private Instant refundCreatedAt;
    @Column(name = "next_attempt", nullable = false)
    private Instant nextAttempt;
    @Column(name = "lease_until")
    private Instant leaseUntil;
    @Column(name = "lease_token")
    private String leaseToken;
    @Column(nullable = false)
    private int attempts;
    @Column(name = "last_outcome", length = 64)
    private String lastOutcome;

    protected PaymentJpaEntity() {
    }

    static PaymentJpaEntity create(UUID id, Instant createdAt) {
        var entity = new PaymentJpaEntity();
        entity.id = id;
        entity.nextAttempt = createdAt.plusSeconds(60);
        return entity;
    }

    void update(UUID orderId, UUID buyerId, BigDecimal amount, String currency, String transactionRef, Instant createdAt, Instant expiresAt, Payment.Status status, String providerTransactionNo, Payment.RefundStatus refundStatus, String refundRequestId, String refundActor, String refundReason, Instant refundCreatedAt) {
        this.orderId = orderId;
        this.buyerId = buyerId;
        this.amount = amount;
        this.currency = currency;
        this.transactionRef = transactionRef;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.status = status;
        this.providerTransactionNo = providerTransactionNo;
        if (this.refundStatus != refundStatus && refundStatus == Payment.RefundStatus.REQUESTED) {
            attempts = 0;
            nextAttempt = refundCreatedAt;
        }
        this.refundStatus = refundStatus;
        this.refundRequestId = refundRequestId;
        this.refundActor = refundActor;
        this.refundReason = refundReason;
        this.refundCreatedAt = refundCreatedAt;
    }

    record ReconciliationLease(String token, int attempts) {
    }

    ReconciliationLease claimReconciliation(Instant now) {
        if (nextAttempt.isAfter(now) || (leaseUntil != null && !leaseUntil.isBefore(now)))
            return null;
        leaseToken = java.util.UUID.randomUUID().toString();
        leaseUntil = now.plusSeconds(120);
        attempts++;
        return new ReconciliationLease(leaseToken, attempts);
    }

    boolean ownsLease(String token) {
        return java.util.Objects.equals(leaseToken, token);
    }

    void completeReconciliation(String token, Instant next, String outcome) {
        if (!ownsLease(token))
            return;
        leaseToken = null;
        leaseUntil = null;
        nextAttempt = next;
        lastOutcome = outcome;
    }

}
