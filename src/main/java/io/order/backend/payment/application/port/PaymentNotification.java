package io.order.backend.payment.application.port;

import java.math.BigDecimal;

/** A decoded provider notification; unverified or invalid data must never settle a payment. */
public record PaymentNotification(boolean signatureValid, boolean dataValid, String transactionReference,
        BigDecimal amount, String currency, boolean successful, String providerTransactionNo) {
}
