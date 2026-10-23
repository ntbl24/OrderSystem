package io.order.backend.order.infrastructure.scheduling;

import io.order.backend.order.application.port.in.ExpireOrderUseCase;

import io.order.backend.order.application.port.out.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "app.workers.enabled", havingValue = "true", matchIfMissing = true)
public class OrderExpiryWorker {
    private final OrderRepository orders;
    private final ExpireOrderUseCase expiry;

    @Scheduled(fixedDelayString = "${purchase.expiry-delay-ms:30000}")
    public void expire() {
        for (String id : orders.expiredIds(Instant.now()))
            expiry.expire(id);
    }
}
