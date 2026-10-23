package io.order.backend.payment.application.usecase;

import io.order.backend.payment.application.port.in.RefundIntentUseCase;
import io.order.backend.payment.application.port.out.PayableOrderPort;
import io.order.backend.payment.application.port.out.PaymentStore;
import org.springframework.stereotype.Service;

@Service
public class RefundIntentService implements RefundIntentUseCase {
    private final PaymentStore store;
    private final PayableOrderPort orders;

    public RefundIntentService(PaymentStore store, PayableOrderPort orders) {
        this.store = store;
        this.orders = orders;
    }

    @Override
    public void acknowledge(String paymentId) {
        var payment = store.find(paymentId).orElseThrow();
        orders.lockForCallback(payment.orderId());
        store.lock(payment.id());
        // The outbox dispatcher owns the transaction; intent and work state are already durable.
    }
}
