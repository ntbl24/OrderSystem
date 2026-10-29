package io.order.backend.payment.application.usecase;

import io.order.backend.common.application.port.CurrentBuyerPort;
import io.order.backend.common.application.port.IdempotencyPort;
import io.order.backend.common.application.port.OutboxPort;
import io.order.backend.payment.application.port.in.*;
import io.order.backend.payment.application.port.out.*;
import io.order.backend.payment.domain.Payment;
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
    public PaymentResult refund(String id, String key, String reason) {
        if (key == null || key.isBlank() || key.length() > 128)
            throw new IllegalArgumentException("Invalid idempotency key");
        String actor = buyer.buyerId();
        var found = store.find(id).orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        orders.lockForCallback(found.getOrderIdentifier());
        return keys.execute(actor, "FULL_REFUND", key, new RefundPaymentCommand(id, reason), PaymentResult.class, () -> {
            var p = store.lock(id);
            boolean requested = p.getRefundStatus() == Payment.RefundStatus.NONE;
            var next = p.requestRefund(UUID.randomUUID().toString().replace("-", ""), actor, reason, Instant.now());
            store.save(next);
            if (requested)
                outbox.enqueue("PAYMENT_REFUND_REQUESTED", id, json.writeValueAsString(Map.of("paymentId", id)));
            return PaymentResult.from(next, null);
        });
    }

}
