package io.order.backend.payment.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

import io.order.backend.order.domain.OrderId;
import io.order.backend.user.domain.UserId;

public final class Payment {
    private final PaymentId id;
    private final OrderId orderId;
    private final UserId buyerId;
    private final BigDecimal amount;
    private final String currency;
    private final TransactionRef transactionRef;
    private final Instant createdAt;
    private final Instant expiresAt;
    private Status status;
    private String providerTransactionNo;
    private RefundStatus refundStatus;
    private String refundRequestId;
    private String refundActor;
    private String refundReason;
    private Instant refundCreatedAt;
    public enum Status {
        PENDING, FAILED, SUCCEEDED
    }

    public enum RefundStatus {
        NONE, REQUESTED, DISPATCHING, UNCERTAIN, ACCEPTED, REFUNDED, REJECTED, MANUAL_REVIEW
    }

    private Payment(PaymentId id, OrderId orderId, UserId buyerId, BigDecimal amount, String currency, TransactionRef transactionRef, Instant createdAt, Instant expiresAt, Status status, String providerTransactionNo, RefundStatus refundStatus, String refundRequestId, String refundActor, String refundReason, Instant refundCreatedAt) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(orderId);
        Objects.requireNonNull(buyerId);
        if (!"VND".equals(currency) || amount == null || amount.signum() <= 0)
            throw new IllegalArgumentException("Positive VND amount required");
        amount = amount.setScale(0, java.math.RoundingMode.UNNECESSARY);
        Math.multiplyExact(amount.longValueExact(), 100L);
        this.id = id;
        this.orderId = orderId;
        this.buyerId = buyerId;
        this.amount = amount;
        this.currency = currency;
        this.transactionRef = Objects.requireNonNull(transactionRef, "Provider reference is required");
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.status = status;
        this.providerTransactionNo = providerTransactionNo;
        this.refundStatus = refundStatus;
        this.refundRequestId = refundRequestId;
        this.refundActor = refundActor;
        this.refundReason = refundReason;
        this.refundCreatedAt = refundCreatedAt;
        Objects.requireNonNull(status, "Payment status is required");
        Objects.requireNonNull(refundStatus, "Refund status is required");
        Objects.requireNonNull(createdAt, "Creation time is required");
        Objects.requireNonNull(expiresAt, "Expiry is required");
    }

    public static Payment reconstitute(PaymentId id, OrderId orderId, UserId buyerId, BigDecimal amount, String currency, TransactionRef transactionRef, Instant createdAt, Instant expiresAt, Status status, String providerTransactionNo, RefundStatus refundStatus, String refundRequestId, String refundActor, String refundReason, Instant refundCreatedAt) {
        return new Payment(id, orderId, buyerId, amount, currency, transactionRef, createdAt, expiresAt, status, providerTransactionNo, refundStatus, refundRequestId, refundActor, refundReason, refundCreatedAt);
    }

    public static Payment create(OrderId orderId, UserId buyerId, BigDecimal amount, String currency,
            Instant now, Instant orderExpiresAt) {
        Objects.requireNonNull(now, "Creation time is required");
        Objects.requireNonNull(orderExpiresAt, "Order expiry is required");
        Instant expires = orderExpiresAt.isBefore(now.plusSeconds(900)) ? orderExpiresAt : now.plusSeconds(900);
        if (!expires.isAfter(now))
            throw new IllegalStateException("Order expired");
        return new Payment(PaymentId.generate(), orderId, buyerId, amount, currency,
                TransactionRef.generate(), now, expires, Status.PENDING, null,
                RefundStatus.NONE, null, null, null, null);
    }

    public boolean requiresReconciliation() {
        return status == Status.PENDING || switch (refundStatus) {
            case REQUESTED, DISPATCHING, ACCEPTED, UNCERTAIN -> true;
            default -> false;
        };
    }

    public Payment exhaustReconciliation() {
        if (refundStatus == RefundStatus.DISPATCHING)
            refundState(RefundStatus.UNCERTAIN);
        if (refundStatus == RefundStatus.ACCEPTED || refundStatus == RefundStatus.UNCERTAIN)
            refundState(RefundStatus.MANUAL_REVIEW);
        return this;
    }

    public long providerAmount() {
        return Math.multiplyExact(amount.longValueExact(), 100L);
    }

    public Payment observe(boolean success, String providerNo) {
        if (status == Status.SUCCEEDED) {
            if (success && !Objects.equals(providerTransactionNo, providerNo))
                throw new IllegalArgumentException("Conflicting provider transaction");
            return this;
        }
        if (success && (providerNo == null || !providerNo.matches("[0-9]{1,32}") || "0".equals(providerNo)))
            throw new IllegalArgumentException("Invalid provider transaction");
        status = success ? Status.SUCCEEDED : Status.FAILED;
        if (success) providerTransactionNo = providerNo;
        return this;
    }

    public Payment requestRefund(String requestId, String actor, String reason, Instant now) {
        if (status != Status.SUCCEEDED)
            throw new IllegalStateException("Only settled payments can be refunded");
        if (refundStatus != RefundStatus.NONE)
            return this;
        if (requestId == null || requestId.isBlank() || requestId.length() > 32)
            throw new IllegalArgumentException("Invalid refund request ID");
        if (actor == null || actor.isBlank())
            throw new IllegalArgumentException("Invalid refund actor");
        if (reason == null || reason.isBlank() || reason.length() > 255 || reason.contains("|")
                || reason.contains("\n") || reason.contains("\r"))
            throw new IllegalArgumentException("Invalid refund reason");
        Objects.requireNonNull(now, "Refund creation time is required");
        refundStatus = RefundStatus.REQUESTED;
        refundRequestId = requestId;
        refundActor = actor;
        refundReason = reason;
        refundCreatedAt = now;
        return this;
    }

    public Payment refundState(RefundStatus next) {
        boolean allowed = switch (refundStatus) {
            case REQUESTED -> next == RefundStatus.DISPATCHING;
            case DISPATCHING ->
                next == RefundStatus.ACCEPTED || next == RefundStatus.UNCERTAIN || next == RefundStatus.REJECTED;
            case ACCEPTED, UNCERTAIN -> next == RefundStatus.REFUNDED || next == RefundStatus.MANUAL_REVIEW;
            default -> false;
        };
        if (!allowed)
            throw new IllegalStateException("Invalid refund transition");
        refundStatus = next;
        return this;
    }

    public PaymentId getId() { return id; }
    public OrderId getOrderId() { return orderId; }
    public UserId getBuyerId() { return buyerId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public TransactionRef getTransactionRef() { return transactionRef; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public Status getStatus() { return status; }
    public String getProviderTransactionNo() { return providerTransactionNo; }
    public RefundStatus getRefundStatus() { return refundStatus; }
    public String getRefundRequestId() { return refundRequestId; }
    public String getRefundActor() { return refundActor; }
    public String getRefundReason() { return refundReason; }
    public Instant getRefundCreatedAt() { return refundCreatedAt; }
}
