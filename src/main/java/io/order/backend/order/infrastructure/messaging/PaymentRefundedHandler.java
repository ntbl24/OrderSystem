package io.order.backend.order.infrastructure.messaging;

import io.order.backend.common.application.port.OutboxHandler;
import io.order.backend.order.application.port.in.PaymentEventUseCase;
import io.order.backend.order.application.port.in.PaymentEventUseCase.PaymentEvent;
import tools.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRefundedHandler implements OutboxHandler {
    private final PaymentEventUseCase payments;
    private final JsonMapper json;

    public String eventType() {
        return "PAYMENT_REFUNDED";
    }

    public void handle(String eventId, String aggregateId, String payload) {
        var event = json.readValue(payload, PaymentEvent.class);
        if (!aggregateId.equals(event.paymentId()))
            throw new IllegalArgumentException("Order event mismatch");
        payments.paymentRefunded(event);
    }
}