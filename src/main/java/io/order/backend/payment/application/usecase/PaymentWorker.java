package io.order.backend.payment.application.usecase;

import io.order.backend.payment.application.port.in.*;
import io.order.backend.payment.application.port.out.*;
import io.order.backend.payment.domain.Payment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.time.Instant;

@Service
public class PaymentWorker implements PaymentWorkUseCase {
    private final PaymentRepository store;
    private final PayableOrderPort orders;
    private final PaymentGateway gateway;
    private final RecordPaymentObservationUseCase payments;
    private final io.order.backend.common.application.port.OutboxPort outbox;
    private final tools.jackson.databind.json.JsonMapper json;
    private final TransactionTemplate tx;

    public PaymentWorker(PaymentRepository store, PayableOrderPort orders, PaymentGateway gateway, RecordPaymentObservationUseCase payments,
            io.order.backend.common.application.port.OutboxPort outbox, tools.jackson.databind.json.JsonMapper json,
            PlatformTransactionManager manager) {
        this.store = store;
        this.orders = orders;
        this.gateway = gateway;
        this.payments = payments;
        this.outbox = outbox;
        this.json = json;
        this.tx = new TransactionTemplate(manager);
    }

    @Override
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
            orders.lockForCallback(p.getOrderIdentifier());
            var claimed = store.claim(id, Instant.now());
            if (claimed == null) return null;
            var payment = claimed.payment();
            if (claimed.attempts() > 8) {
                payment.exhaustReconciliation();
                store.save(payment);
                store.complete(id, claimed.token(), Instant.now(), "MANUAL_REVIEW_EXHAUSTED_LEASE");
                return null;
            }
            if (!payment.requiresReconciliation()) {
                store.complete(id, claimed.token(), Instant.now(), "NO_WORK");
                return null;
            }
            boolean abandoned = claimed.abandonedRefund();
            if (payment.getRefundStatus() == Payment.RefundStatus.REQUESTED) {
                payment.refundState(Payment.RefundStatus.DISPATCHING);
                store.save(payment);
            }
            return new PaymentRepository.Work(payment, claimed.token(), claimed.attempts(), abandoned);
        });
        if (work == null)
            return;
        Payment p = work.payment();
        boolean sendRefund = p.getRefundStatus() == Payment.RefundStatus.DISPATCHING && !work.abandonedRefund();
        // No transaction spans either HTTP call. Expired refund leases query, never
        // resend.
        var refund = sendRefund ? gateway.refund(p) : null;
        var observation = sendRefund ? null : gateway.query(p);
        tx.executeWithoutResult(s -> {
            orders.lockForCallback(p.getOrderIdentifier());
            var current = store.lock(id);
            if (!store.ownsLease(id, work.token()))
                return;
            if (sendRefund && current.getRefundStatus() == Payment.RefundStatus.DISPATCHING) {
                var state = switch (refund) {
                    case ACCEPTED -> Payment.RefundStatus.ACCEPTED;
                    case REJECTED -> Payment.RefundStatus.REJECTED;
                    case UNCERTAIN -> Payment.RefundStatus.UNCERTAIN;
                };
                store.save(current.refundState(state));
            } else if (!sendRefund) {
                if (current.getRefundStatus() == Payment.RefundStatus.DISPATCHING) {
                    current.refundState(Payment.RefundStatus.UNCERTAIN);
                }
                if (current.getRefundStatus() == Payment.RefundStatus.ACCEPTED
                        || current.getRefundStatus() == Payment.RefundStatus.UNCERTAIN) {
                    if (observation.state() == PaymentGateway.Observation.State.REFUNDED) {
                        var next = current.refundState(Payment.RefundStatus.REFUNDED);
                        store.save(next);
                        outbox.enqueue("PAYMENT_REFUNDED", next.getId(), json.writeValueAsString(
                                new PaymentEvent(next.getOrderId(), next.getId(), next.getAmount(), next.getCurrency())));
                    } else {
                        if (work.attempts() >= 8)
                            current.refundState(Payment.RefundStatus.MANUAL_REVIEW);
                        store.save(current);
                    }
                } else if (current.getStatus() != Payment.Status.SUCCEEDED) {
                    if (observation.state() == PaymentGateway.Observation.State.SUCCEEDED)
                        payments.recordObservation(current, true, observation.providerTransactionNo());
                    else if (observation.state() == PaymentGateway.Observation.State.FAILED)
                        payments.recordObservation(current, false, null);
                }
            }
            String outcome = sendRefund ? "REFUND_" + refund : observation.state().name();
            if (work.attempts() >= 8 && current.getStatus() == Payment.Status.PENDING)
                outcome = "MANUAL_REVIEW_RECONCILIATION";
            store.complete(id, work.token(),
                    Instant.now().plusSeconds(Math.min(3600, 60L << Math.min(work.attempts(), 6))), outcome);
        });
    }
}
