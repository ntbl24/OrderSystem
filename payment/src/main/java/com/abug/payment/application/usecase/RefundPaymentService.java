package com.abug.payment.application.usecase;

import com.abug.common.application.port.CurrentBuyerPort;
import com.abug.common.application.port.IdempotencyPort;
import com.abug.common.application.port.OutboxPort;
import com.abug.common.exception.AppException;
import com.abug.common.exception.ErrorCode;
import com.abug.payment.application.port.in.*;
import com.abug.payment.application.port.out.*;
import com.abug.payment.domain.Payment;
import com.abug.payment.domain.PaymentId;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;
import java.time.Instant;
import java.util.*;

@Service
@lombok.RequiredArgsConstructor
public class RefundPaymentService implements RefundPaymentUseCase {
    private final CurrentBuyerPort buyer;
    private final IdempotencyPort keys;
    private final PayableOrderPort orders;
    private final PaymentRepository store;
    private final OutboxPort outbox;
    private final JsonMapper json;
    @Transactional
    public PaymentResult refund(PaymentId id, String key, String reason) {
        if (key == null || key.isBlank() || key.length() > 128)
            throw new AppException(ErrorCode.INVALID_INPUT, "Invalid idempotency key");
        String actor = buyer.buyerId();
        var found = store.find(id).orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND, "Payment not found"));
        orders.lockForCallback(found.getOrderId());
        return keys.execute(actor, "FULL_REFUND", key, new RefundPaymentCommand(id, reason), PaymentResult.class, () -> {
            var p = store.lock(id);
            boolean requested = p.getRefundStatus() == Payment.RefundStatus.NONE;
            var next = p.requestRefund(UUID.randomUUID().toString().replace("-", ""), actor, reason, Instant.now());
            store.save(next);
            if (requested)
                outbox.enqueue("PAYMENT_REFUND_REQUESTED", id.toString(), json.writeValueAsString(Map.of("paymentId", id.toString())));
            return PaymentResult.from(next, null);
        });
    }

}
