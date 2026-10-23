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
public class PaymentService implements PaymentUseCase {
    private final CurrentBuyerPort buyer;
    private final IdempotencyPort keys;
    private final PayableOrderPort orders;
    private final PaymentStore store;
    private final PaymentGateway gateway;
    private final OutboxPort outbox;
    private final JsonMapper json;

    public PaymentService(CurrentBuyerPort buyer, IdempotencyPort keys, PayableOrderPort orders, PaymentStore store,
            PaymentGateway gateway, OutboxPort outbox, JsonMapper json) {
        this.buyer = buyer;
        this.keys = keys;
        this.orders = orders;
        this.store = store;
        this.gateway = gateway;
        this.outbox = outbox;
        this.json = json;
    }

    @Transactional
    public PaymentResult create(String orderId, String key, String ip) {
        String buyerId = buyer.buyerId();
        if (orderId == null || orderId.isBlank() || orderId.length() > 255)
            throw new IllegalArgumentException("Invalid order ID");
        if (key == null || key.isBlank() || key.length() > 128)
            throw new IllegalArgumentException("Invalid idempotency key");
        // Replay is scoped to the same authenticated buyer by the shared idempotency
        // service.
        orders.lockForCallback(orderId);
        return keys.execute(buyerId, "PAYMENT_ATTEMPT", key, new CreatePaymentCommand(orderId), PaymentResult.class, () -> {
            var order = orders.lockForPayment(orderId, buyerId);
            if (store.orderSettled(orderId))
                throw new IllegalStateException("Order already paid");
            Instant now = Instant.now();
            Instant expires = order.expiresAt().isBefore(now.plusSeconds(900)) ? order.expiresAt()
                    : now.plusSeconds(900);
            if (!expires.isAfter(now))
                throw new IllegalStateException("Order expired");
            var p = new Payment(UUID.randomUUID().toString(), orderId, buyerId, order.amount(), order.currency(),
                    UUID.randomUUID().toString().replace("-", ""), now, expires, Payment.Status.PENDING, null,
                    Payment.RefundStatus.NONE, null, null, null, null);
            store.save(p);
            return PaymentResult.from(p, gateway.paymentUrl(p, ip));
        });
    }

    @Transactional(readOnly = true)
    public PaymentResult get(String id) {
        var p = store.find(id).orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        if (!p.buyerId().equals(buyer.buyerId()))
            throw new org.springframework.security.access.AccessDeniedException("Payment does not belong to buyer");
        return PaymentResult.from(p, null);
    }

    @Transactional
    public NotificationResult ipn(PaymentNotification notification) {
        if (!notification.signatureValid())
            return NotificationResult.INVALID_SIGNATURE;
        var found = store.byReference(notification.transactionReference());
        if (found.isEmpty())
            return NotificationResult.NOT_FOUND;
        if (!matches(found.get(), notification))
            return NotificationResult.INVALID_DATA;
        orders.lockForCallback(found.get().orderId());
        var p = store.lock(found.get().id());
        if (p.status() == Payment.Status.SUCCEEDED) {
            if (notification.successful() && !Objects.equals(p.providerTransactionNo(), notification.providerTransactionNo()))
                return NotificationResult.CONFLICTING_TRANSACTION;
            return NotificationResult.ALREADY_CONFIRMED;
        }
        recordObservation(p, notification.successful(), notification.providerTransactionNo());
        return NotificationResult.CONFIRMED;
    }

    @Transactional(readOnly = true)
    public ReturnView returnView(PaymentNotification notification) {
        if (!notification.signatureValid())
            return new ReturnView(false, "INVALID", null);
        var p = store.byReference(notification.transactionReference());
        if (p.isEmpty() || !matches(p.get(), notification))
            return new ReturnView(false, "INVALID", null);
        return new ReturnView(true, p.get().status().name(),
                "Payment confirmation is processed through IPN, not this page");
    }

    @Transactional
    public PaymentResult refund(String id, String key, String reason) {
        if (key == null || key.isBlank() || key.length() > 128)
            throw new IllegalArgumentException("Invalid idempotency key");
        if (reason == null || reason.isBlank() || reason.length() > 255 || reason.contains("|") || reason.contains("\n")
                || reason.contains("\r"))
            throw new IllegalArgumentException("Invalid refund reason");
        String actor = buyer.buyerId();
        var found = store.find(id).orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        orders.lockForCallback(found.orderId());
        return keys.execute(actor, "FULL_REFUND", key, new RefundPaymentCommand(id, reason), PaymentResult.class, () -> {
            var p = store.lock(id);
            var next = p.requestRefund(UUID.randomUUID().toString().replace("-", ""), actor, reason, Instant.now());
            store.save(next);
            if (p.refundStatus() == Payment.RefundStatus.NONE)
                outbox.enqueue("PAYMENT_REFUND_REQUESTED", id, json.writeValueAsString(Map.of("paymentId", id)));
            return PaymentResult.from(next, null);
        });
    }

    void recordObservation(Payment p, boolean success, String providerNo) {
        var next = p.observe(success, providerNo);
        store.save(next);
        if (success && p.status() != Payment.Status.SUCCEEDED)
            emit("PAYMENT_SUCCEEDED", next);
    }

    void emit(String type, Payment p) {
        outbox.enqueue(type, p.id(),
                json.writeValueAsString(new PaymentEvent(p.orderId(), p.id(), p.amount(), p.currency())));
    }

    private static boolean matches(Payment payment, PaymentNotification notification) {
        return notification.dataValid() && payment.transactionRef().equals(notification.transactionReference())
                && notification.amount() != null && payment.amount().compareTo(notification.amount()) == 0
                && payment.currency().equals(notification.currency());
    }

    public record PaymentEvent(String orderId, String paymentId, java.math.BigDecimal amount, String currency) {
    }
}
