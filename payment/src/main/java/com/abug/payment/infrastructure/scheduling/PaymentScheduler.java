package com.abug.payment.infrastructure.scheduling;

import com.abug.payment.application.port.in.PaymentWorkUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.workers.enabled", havingValue = "true", matchIfMissing = true)
public class PaymentScheduler {
    private final PaymentWorkUseCase payments;

    public PaymentScheduler(PaymentWorkUseCase payments) {
        this.payments = payments;
    }

    @Scheduled(fixedDelayString = "${payment.worker-delay-ms:10000}")
    public void tick() {
        payments.tick();
    }
}
