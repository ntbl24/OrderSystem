package io.order.backend.payment.application.port.in;

import io.order.backend.common.exception.AppException;
import io.order.backend.common.exception.ErrorCode;
import io.order.backend.payment.domain.PaymentId;

public record RefundPaymentCommand(PaymentId paymentId, String reason) {
    public RefundPaymentCommand {
        if (reason == null || reason.isBlank() || reason.length() > 255 || reason.contains("|")
                || reason.contains("\n") || reason.contains("\r"))
            throw new AppException(ErrorCode.INVALID_INPUT, "Invalid refund reason");
    }
}
