package io.order.backend.payment.infrastructure.messaging;

import io.order.backend.common.application.port.OutboxHandler;
import io.order.backend.payment.application.port.in.RefundIntentUseCase;
import org.springframework.stereotype.Component;

@Component
public class RefundIntentHandler implements OutboxHandler {
    private final RefundIntentUseCase refunds;

    public RefundIntentHandler(RefundIntentUseCase refunds) {
        this.refunds = refunds;
    }

    public String eventType() {
        return "PAYMENT_REFUND_REQUESTED";
    }

    public void handle(String eventId, String aggregateId, String payload) {
        refunds.acknowledge(aggregateId);
    }
}
