package io.order.backend.payment.application.usecase;

import io.order.backend.common.application.port.OutboxPort;
import io.order.backend.payment.application.port.in.*;
import io.order.backend.payment.application.port.out.*;
import io.order.backend.payment.domain.Payment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;
import java.util.*;

@Service
@lombok.RequiredArgsConstructor
public class PaymentCallbackService implements PaymentCallbackUseCase, RecordPaymentObservationUseCase {
    private final PayableOrderPort orders;
    private final PaymentRepository store;
    private final OutboxPort outbox;
    private final JsonMapper json;

    @Override
    @Transactional
    public NotificationResult ipn(PaymentNotification notification) {
        if (!notification.signatureValid())
            return NotificationResult.INVALID_SIGNATURE;
        var found = store.byReference(notification.transactionReference());
        if (found.isEmpty())
            return NotificationResult.NOT_FOUND;
        if (!matches(found.get(), notification))
            return NotificationResult.INVALID_DATA;
        orders.lockForCallback(found.get().getOrderIdentifier());
        var p = store.lock(found.get().getId());
        if (p.getStatus() == Payment.Status.SUCCEEDED) {
            if (notification.successful() && !Objects.equals(p.getProviderTransactionNo(), notification.providerTransactionNo()))
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
        return new ReturnView(true, p.get().getStatus().name(),
                "Payment confirmation is processed through IPN, not this page");
    }

    @Override
    @Transactional
    public void recordObservation(Payment p, boolean success, String providerNo) {
        boolean settled = p.getStatus() == Payment.Status.SUCCEEDED;
        var next = p.observe(success, providerNo);
        store.save(next);
        if (success && !settled)
            emit("PAYMENT_SUCCEEDED", next);
    }

    private void emit(String type, Payment p) {
        outbox.enqueue(type, p.getId(),
                json.writeValueAsString(new PaymentEvent(p.getOrderId(), p.getId(), p.getAmount(), p.getCurrency())));
    }

    private static boolean matches(Payment payment, PaymentNotification notification) {
        return notification.dataValid() && payment.getTransactionRef().equals(notification.transactionReference())
                && notification.amount() != null && payment.getAmount().compareTo(notification.amount()) == 0
                && payment.getCurrency().equals(notification.currency());
    }

}
