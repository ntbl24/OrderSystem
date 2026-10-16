package io.order.backend.order.application;

import io.order.backend.order.application.port.out.OrderRepository;
import io.order.backend.order.application.port.out.InventoryPort;
import io.order.backend.common.messaging.OutboxHandler;
import tools.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class PaymentSucceededHandler implements OutboxHandler {
    private final OrderRepository orders;
    private final InventoryPort inventory;
    private final JsonMapper json;

    public String eventType() {
        return "PAYMENT_SUCCEEDED";
    }

    public record Event(String orderId, String paymentId, BigDecimal amount, String currency) {
    }

    @Transactional
    public void handle(String eventId, String aggregateId, String payload) {
        var event = json.readValue(payload, Event.class);
        if (!aggregateId.equals(event.paymentId()))
            throw new IllegalArgumentException("Order event mismatch");
        var order = orders.lockById(event.orderId()).orElseThrow();
        if (order.acceptPayment(event.paymentId(), event.amount(), event.currency(), Instant.now()))
            InventorySettlement.settle(order, inventory, "CONSUMED");
        else if ("PAYMENT_REVIEW".equals(order.getOrderStatus()) && "HELD".equals(order.getReservationStatus()))
            InventorySettlement.settle(order, inventory, "RELEASED");
        orders.save(order);
    }
}
