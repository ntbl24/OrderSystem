package com.abug.payment.application.port.in;

import com.abug.payment.domain.Payment;

public interface RecordPaymentObservationUseCase {
    void recordObservation(Payment payment, boolean success, String providerTransactionNo);
}
