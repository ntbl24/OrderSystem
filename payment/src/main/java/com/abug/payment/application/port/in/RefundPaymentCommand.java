package com.abug.payment.application.port.in;

import com.abug.common.exception.AppException;
import com.abug.common.exception.ErrorCode;
import com.abug.payment.domain.PaymentId;

public record RefundPaymentCommand(PaymentId paymentId, String reason) {
    public RefundPaymentCommand {
        if (reason == null || reason.isBlank() || reason.length() > 255 || reason.contains("|")
                || reason.contains("\n") || reason.contains("\r"))
            throw new AppException(ErrorCode.INVALID_INPUT, "Invalid refund reason");
    }
}
