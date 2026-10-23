package io.order.backend.payment.infrastructure.persistence;

import io.order.backend.payment.application.port.out.PaymentStore;
import io.order.backend.payment.domain.Payment;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.*;

@Repository
@org.springframework.transaction.annotation.Transactional(propagation = org.springframework.transaction.annotation.Propagation.MANDATORY)
public class JpaPaymentStore implements PaymentStore {
    private final EntityManager em;

    public JpaPaymentStore(EntityManager em) {
        this.em = em;
    }

    public Optional<Payment> find(String id) {
        return Optional.ofNullable(em.find(PaymentJpaEntity.class, id)).map(PaymentJpaEntity::domain);
    }

    public Optional<Payment> byReference(String ref) {
        return em.createQuery("select p from PaymentJpaEntity p where p.transactionRef=:ref", PaymentJpaEntity.class)
                .setParameter("ref", ref).getResultStream().findFirst().map(PaymentJpaEntity::domain);
    }

    public Payment lock(String id) {
        var e = em.find(PaymentJpaEntity.class, id);
        if (e == null)
            throw new IllegalArgumentException("Payment not found");
        // A lookup before the order lock may have populated a stale first-level-cache
        // snapshot.
        em.refresh(e, LockModeType.PESSIMISTIC_WRITE);
        return e.domain();
    }

    public void save(Payment p) {
        var e = em.find(PaymentJpaEntity.class, p.id());
        if (e == null)
            em.persist(PaymentJpaEntity.create(p));
        else
            e.apply(p);
        em.flush();
    }

    public boolean orderSettled(String id) {
        return em.createQuery("select count(p) from PaymentJpaEntity p where p.orderId=:id and p.status=:s", Long.class)
                .setParameter("id", id).setParameter("s", Payment.Status.SUCCEEDED).getSingleResult() > 0;
    }

    public List<String> due(Instant now, int limit) {
        return em.createQuery(
                "select p.id from PaymentJpaEntity p where p.nextAttempt<=:now and (p.leaseUntil is null or p.leaseUntil<:now) and (p.attempts<8 or p.leaseToken is not null) and (p.status=:pending or p.refundStatus in :refunds) order by p.nextAttempt,p.id",
                String.class)
                .setParameter("now", now).setParameter("pending", Payment.Status.PENDING)
                .setParameter("refunds",
                        List.of(Payment.RefundStatus.REQUESTED, Payment.RefundStatus.DISPATCHING,
                                Payment.RefundStatus.ACCEPTED, Payment.RefundStatus.UNCERTAIN))
                .setMaxResults(limit).getResultList();
    }

    public Work claim(String id, Instant now) {
        var e = em.find(PaymentJpaEntity.class, id);
        if (e == null)
            return null;
        em.refresh(e, LockModeType.PESSIMISTIC_WRITE);
        if (e.nextAttempt.isAfter(now) || (e.leaseUntil != null && !e.leaseUntil.isBefore(now)))
            return null;
        if (e.attempts >= 8) {
            var p = e.domain();
            if (p.refundStatus() == Payment.RefundStatus.DISPATCHING)
                p = p.refundState(Payment.RefundStatus.UNCERTAIN);
            if (p.refundStatus() == Payment.RefundStatus.ACCEPTED || p.refundStatus() == Payment.RefundStatus.UNCERTAIN)
                p = p.refundState(Payment.RefundStatus.MANUAL_REVIEW);
            e.apply(p);
            e.leaseToken = null;
            e.leaseUntil = null;
            e.lastOutcome = "MANUAL_REVIEW_EXHAUSTED_LEASE";
            return null;
        }
        if (e.status != Payment.Status.PENDING
                && !List.of(Payment.RefundStatus.REQUESTED, Payment.RefundStatus.DISPATCHING,
                        Payment.RefundStatus.ACCEPTED, Payment.RefundStatus.UNCERTAIN).contains(e.refundStatus))
            return null;
        boolean abandoned = e.refundStatus == Payment.RefundStatus.DISPATCHING;
        if (e.refundStatus == Payment.RefundStatus.REQUESTED)
            e.apply(e.domain().refundState(Payment.RefundStatus.DISPATCHING));
        e.leaseToken = UUID.randomUUID().toString();
        e.leaseUntil = now.plusSeconds(120);
        e.attempts++;
        return new Work(e.domain(), e.leaseToken, e.attempts, abandoned);
    }

    public boolean ownsLease(String id, String token) {
        return Objects.equals(em.find(PaymentJpaEntity.class, id).leaseToken, token);
    }

    public void complete(String id, String token, Instant next, String outcome) {
        var e = em.find(PaymentJpaEntity.class, id, LockModeType.PESSIMISTIC_WRITE);
        if (!Objects.equals(e.leaseToken, token))
            return;
        e.leaseToken = null;
        e.leaseUntil = null;
        e.nextAttempt = next;
        e.lastOutcome = outcome;
    }
}
