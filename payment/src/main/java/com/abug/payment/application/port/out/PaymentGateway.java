package com.abug.payment.application.port.out;

import com.abug.payment.domain.Payment;

public interface PaymentGateway {
    String paymentUrl(Payment payment, String ipAddress);

    Observation query(Payment payment);

    RefundOutcome refund(Payment payment);

    enum RefundOutcome {
        ACCEPTED, REJECTED, UNCERTAIN
    }

    record Observation(State state, String providerTransactionNo) {
        public enum State {
            SUCCEEDED, FAILED, PENDING, REFUNDED, UNKNOWN
        }
    }
}
