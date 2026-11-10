package com.abug.payment.application.usecase;

import com.abug.payment.application.port.in.RefundIntentUseCase;
import com.abug.payment.application.port.out.PayableOrderPort;
import com.abug.payment.application.port.out.PaymentRepository;
import com.abug.payment.domain.PaymentId;

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
        var payment = store.find(PaymentId.of(paymentId)).orElseThrow();
        orders.lockForCallback(payment.getOrderId());
        store.lock(payment.getId());
        // The outbox dispatcher owns the transaction; intent and work state are already durable.
    }
}
