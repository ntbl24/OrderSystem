package io.order.backend.payment.application.port.in;

import io.order.backend.payment.domain.Payment;

public interface RecordPaymentObservationUseCase {
    void recordObservation(Payment payment, boolean success, String providerTransactionNo);
}
