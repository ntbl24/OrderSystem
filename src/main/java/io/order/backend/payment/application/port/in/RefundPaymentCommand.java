package io.order.backend.payment.application.port.in;

public record RefundPaymentCommand(String paymentId, String reason) {
    public RefundPaymentCommand {
        if (reason == null || reason.isBlank() || reason.length() > 255 || reason.contains("|")
                || reason.contains("\n") || reason.contains("\r"))
            throw new IllegalArgumentException("Invalid refund reason");
    }
}
