package io.order.backend.payment.application.port.in;

import io.order.backend.common.exception.AppException;
import io.order.backend.common.exception.ErrorCode;
import io.order.backend.order.domain.OrderId;

public record CreatePaymentCommand(OrderId orderId, String key, String ipAddress)   {

    public CreatePaymentCommand {
        java.util.Objects.requireNonNull(orderId, "Order ID is required");
        if (key == null || key.isBlank() || key.length() > 128)
            throw new AppException(ErrorCode.INVALID_INPUT, "Invalid idempotency key");
    }
    
}
