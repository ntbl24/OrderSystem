package com.abug.order.infrastructure.messaging;

import com.abug.common.application.port.OutboxHandler;
import com.abug.common.exception.AppException;
import com.abug.common.exception.ErrorCode;
import com.abug.order.application.port.in.PaymentEventUseCase;
import com.abug.order.application.port.in.PaymentEventUseCase.PaymentEvent;
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
        if (!aggregateId.equals(event.paymentId().toString()))
            throw new AppException(ErrorCode.BAD_REQUEST, "Order event mismatch");
        payments.paymentRefunded(event);
    }
}