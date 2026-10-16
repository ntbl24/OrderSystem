package io.order.backend.payment.application;

import io.order.backend.common.messaging.OutboxHandler;
import io.order.backend.payment.application.port.*;
import org.springframework.stereotype.Component;

@Component
public class RefundIntentHandler implements OutboxHandler {
    private final PaymentStore store;
    private final PayableOrderPort orders;

    public RefundIntentHandler(PaymentStore store, PayableOrderPort orders) {
        this.store = store;
        this.orders = orders;
    }

    public String eventType() {
        return "PAYMENT_REFUND_REQUESTED";
    }

    public void handle(String eventId, String aggregateId, String payload) {
        var p = store.find(aggregateId).orElseThrow();
        orders.lockForCallback(p.orderId());
        store.lock(p.id());
        // Intent is already durable with its work state; dispatcher only acknowledges
        // delivery.
    }
}
