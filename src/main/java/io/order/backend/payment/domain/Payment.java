package io.order.backend.payment.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Immutable payment attempt; provider observations are not browser-controlled
 * state.
 */
public record Payment(String id, String orderId, String buyerId, BigDecimal amount, String currency,
        String transactionRef, Instant createdAt, Instant expiresAt, Status status,
        String providerTransactionNo, RefundStatus refundStatus, String refundRequestId,
        String refundActor, String refundReason, Instant refundCreatedAt) {
    public enum Status {
        PENDING, FAILED, SUCCEEDED
    }

    public enum RefundStatus {
        NONE, REQUESTED, DISPATCHING, UNCERTAIN, ACCEPTED, REFUNDED, REJECTED, MANUAL_REVIEW
    }

    public Payment {
        Objects.requireNonNull(id);
        Objects.requireNonNull(orderId);
        Objects.requireNonNull(buyerId);
        if (!"VND".equals(currency) || amount == null || amount.signum() <= 0)
            throw new IllegalArgumentException("Positive VND amount required");
        amount = amount.setScale(0, java.math.RoundingMode.UNNECESSARY);
        Math.multiplyExact(amount.longValueExact(), 100L);
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
        return new Payment(id, orderId, buyerId, amount, currency, transactionRef, createdAt, expiresAt,
                success ? Status.SUCCEEDED : Status.FAILED, success ? providerNo : providerTransactionNo,
                refundStatus, refundRequestId, refundActor, refundReason, refundCreatedAt);
    }

    public Payment requestRefund(String requestId, String actor, String reason, Instant now) {
        if (status != Status.SUCCEEDED)
            throw new IllegalStateException("Only settled payments can be refunded");
        if (refundStatus != RefundStatus.NONE)
            return this;
        return new Payment(id, orderId, buyerId, amount, currency, transactionRef, createdAt, expiresAt,
                status, providerTransactionNo, RefundStatus.REQUESTED, requestId, actor, reason, now);
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
        return new Payment(id, orderId, buyerId, amount, currency, transactionRef, createdAt, expiresAt,
                status, providerTransactionNo, next, refundRequestId, refundActor, refundReason, refundCreatedAt);
    }
}
