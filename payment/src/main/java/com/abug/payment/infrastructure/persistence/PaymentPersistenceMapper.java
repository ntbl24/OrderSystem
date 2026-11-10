package com.abug.payment.infrastructure.persistence;

import com.abug.order.domain.OrderId;
import com.abug.payment.domain.Payment;
import com.abug.payment.domain.PaymentId;
import com.abug.payment.domain.TransactionRef;
import com.abug.user.domain.UserId;

public final class PaymentPersistenceMapper {
    private PaymentPersistenceMapper() {}

    public static Payment domain(PaymentJpaEntity entity) {
        return Payment.reconstitute(
                PaymentId.of(entity.getId()),
                OrderId.of(entity.getOrderId()),
                UserId.of(entity.getBuyerId()),
                entity.getAmount(),
                entity.getCurrency(),
                TransactionRef.of(entity.getTransactionRef()),
                entity.getCreatedAt(),
                entity.getExpiresAt(),
                entity.getStatus(),
                entity.getProviderTransactionNo(),
                entity.getRefundStatus(),
                entity.getRefundRequestId(),
                entity.getRefundActor(),
                entity.getRefundReason(),
                entity.getRefundCreatedAt());
    }

    public static PaymentJpaEntity create(Payment payment) {
        var entity = PaymentJpaEntity.create(payment.getId().value(), payment.getCreatedAt());
        copy(payment, entity);
        return entity;
    }

    public static void copy(Payment payment, PaymentJpaEntity entity) {
        entity.update(
                payment.getOrderId().value(),
                payment.getBuyerId().value(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getTransactionRef().value(),
                payment.getCreatedAt(),
                payment.getExpiresAt(),
                payment.getStatus(),
                payment.getProviderTransactionNo(),
                payment.getRefundStatus(),
                payment.getRefundRequestId(),
                payment.getRefundActor(),
                payment.getRefundReason(),
                payment.getRefundCreatedAt());
    }
}
