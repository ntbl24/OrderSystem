package com.abug.payment.application.port.in;


import com.abug.payment.common.exception.AppException;
import com.abug.payment.common.exception.ErrorCode;
import com.abug.payment.domain.OrderId;

public record CreatePaymentCommand(OrderId orderId, String key, String ipAddress)   {

    public CreatePaymentCommand {
        java.util.Objects.requireNonNull(orderId, "Order ID is required");
        if (key == null || key.isBlank() || key.length() > 128)
            throw new AppException(ErrorCode.INVALID_INPUT, "Invalid idempotency key");
    }
    
}
