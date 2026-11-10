package com.abug.order.infrastructure.scheduling;

import com.abug.order.application.port.in.ExpireOrderUseCase;

import com.abug.order.application.port.out.OrderRepository;
import com.abug.order.domain.OrderId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.workers.enabled", havingValue = "true", matchIfMissing = true)
public class OrderExpiryWorker {
    private final OrderRepository orders;
    private final ExpireOrderUseCase expiry;

    @Scheduled(fixedDelayString = "${purchase.expiry-delay-ms:30000}")
    public void expire() {
        for (OrderId id : orders.expiredIds(Instant.now()))
            expiry.expire(id);
    }
}
