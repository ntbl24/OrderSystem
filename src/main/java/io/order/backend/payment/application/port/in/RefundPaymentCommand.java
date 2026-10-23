package io.order.backend.payment.application.port.in;

public record RefundPaymentCommand(String paymentId, String reason) {
}
