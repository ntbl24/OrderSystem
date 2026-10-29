package io.order.backend.payment.application.usecase;

import java.math.BigDecimal;

record PaymentEvent(String orderId, String paymentId, BigDecimal amount, String currency) {}
