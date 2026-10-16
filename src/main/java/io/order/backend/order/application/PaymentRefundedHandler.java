package io.order.backend.order.application;

import io.order.backend.order.application.port.out.OrderRepository;
import io.order.backend.order.application.port.out.InventoryPort;
import io.order.backend.common.messaging.OutboxHandler;
import tools.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentRefundedHandler implements OutboxHandler {
    private final OrderRepository orders;
    private final InventoryPort inventory;
    private final JsonMapper json;

    public String eventType() {
        return "PAYMENT_REFUNDED";
    }

    @Transactional
    public void handle(String eventId, String aggregateId, String payload) {
        var event = json.readValue(payload, PaymentSucceededHandler.Event.class);
        if (!aggregateId.equals(event.paymentId()))
            throw new IllegalArgumentException("Order event mismatch");
        var order = orders.lockById(event.orderId()).orElseThrow();
        if (order.acceptRefund(event.paymentId(), event.amount(), event.currency())
                && "CONSUMED".equals(order.getReservationStatus()))
            InventorySettlement.settle(order, inventory, "RETURNED");
        orders.save(order);
    }
}
