package io.order.backend.payment.application.usecase;

import io.order.backend.payment.application.port.in.RefundIntentUseCase;
import io.order.backend.payment.application.port.out.PayableOrderPort;
import io.order.backend.payment.application.port.out.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class RefundIntentService implements RefundIntentUseCase {
    private final PaymentRepository store;
    private final PayableOrderPort orders;

    public RefundIntentService(PaymentRepository store, PayableOrderPort orders) {
        this.store = store;
        this.orders = orders;
    }

    @Override
    public void acknowledge(String paymentId) {
        var payment = store.find(paymentId).orElseThrow();
        orders.lockForCallback(payment.getOrderIdentifier());
        store.lock(payment.getId());
        // The outbox dispatcher owns the transaction; intent and work state are already durable.
    }
}
