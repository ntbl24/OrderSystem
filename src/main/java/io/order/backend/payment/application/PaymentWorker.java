package io.order.backend.payment.application;

import io.order.backend.payment.application.port.*;
import io.order.backend.payment.domain.Payment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.time.Instant;

@Component
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "app.workers.enabled", havingValue = "true", matchIfMissing = true)
public class PaymentWorker {
    private final PaymentStore store;
    private final PayableOrderPort orders;
    private final PaymentGateway gateway;
    private final PaymentService payments;
    private final TransactionTemplate tx;

    public PaymentWorker(PaymentStore store, PayableOrderPort orders, PaymentGateway gateway, PaymentService payments,
            PlatformTransactionManager manager) {
        this.store = store;
        this.orders = orders;
        this.gateway = gateway;
        this.payments = payments;
        this.tx = new TransactionTemplate(manager);
    }

    @Scheduled(fixedDelayString = "${payment.worker-delay-ms:10000}")
    public void tick() {
        var ids = tx.execute(s -> store.due(Instant.now(), 25));
        if (ids == null)
            return;
        for (String id : ids) {
            try {
                process(id);
            } catch (RuntimeException ignored) {
                /* Persisted lease permits recovery; never repeat an uncertain refund. */ }
        }
    }

    public void process(String id) {
        var work = tx.execute(s -> {
            var p = store.find(id).orElse(null);
            if (p == null)
                return null;
            orders.lockForCallback(p.orderId());
            return store.claim(id, Instant.now());
        });
        if (work == null)
            return;
        Payment p = work.payment();
        boolean sendRefund = p.refundStatus() == Payment.RefundStatus.DISPATCHING && !work.abandonedRefund();
        // No transaction spans either HTTP call. Expired refund leases query, never
        // resend.
        var refund = sendRefund ? gateway.refund(p) : null;
        var observation = sendRefund ? null : gateway.query(p);
        tx.executeWithoutResult(s -> {
            orders.lockForCallback(p.orderId());
            var current = store.lock(id);
            if (!store.ownsLease(id, work.token()))
                return;
            if (sendRefund && current.refundStatus() == Payment.RefundStatus.DISPATCHING) {
                var state = switch (refund) {
                    case ACCEPTED -> Payment.RefundStatus.ACCEPTED;
                    case REJECTED -> Payment.RefundStatus.REJECTED;
                    case UNCERTAIN -> Payment.RefundStatus.UNCERTAIN;
                };
                store.save(current.refundState(state));
            } else if (!sendRefund) {
                if (current.refundStatus() == Payment.RefundStatus.DISPATCHING) {
                    current = current.refundState(Payment.RefundStatus.UNCERTAIN);
                    store.save(current);
                }
                if (current.refundStatus() == Payment.RefundStatus.ACCEPTED
                        || current.refundStatus() == Payment.RefundStatus.UNCERTAIN) {
                    if (observation.state() == PaymentGateway.Observation.State.REFUNDED) {
                        var next = current.refundState(Payment.RefundStatus.REFUNDED);
                        store.save(next);
                        payments.emit("PAYMENT_REFUNDED", next);
                    } else if (work.attempts() >= 8)
                        store.save(current.refundState(Payment.RefundStatus.MANUAL_REVIEW));
                } else if (current.status() != Payment.Status.SUCCEEDED) {
                    if (observation.state() == PaymentGateway.Observation.State.SUCCEEDED)
                        payments.recordObservation(current, true, observation.providerTransactionNo());
                    else if (observation.state() == PaymentGateway.Observation.State.FAILED)
                        payments.recordObservation(current, false, null);
                }
            }
            String outcome = sendRefund ? "REFUND_" + refund : observation.state().name();
            if (work.attempts() >= 8 && current.status() == Payment.Status.PENDING)
                outcome = "MANUAL_REVIEW_RECONCILIATION";
            store.complete(id, work.token(),
                    Instant.now().plusSeconds(Math.min(3600, 60L << Math.min(work.attempts(), 6))), outcome);
        });
    }
}
