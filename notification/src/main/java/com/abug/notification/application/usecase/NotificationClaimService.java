package com.abug.notification.application.usecase;

import com.abug.notification.application.port.in.NotificationClaimUseCase;
import com.abug.notification.application.port.out.ProcessedEventRepository;
import com.abug.notification.domain.DeliveryStatus;
import com.abug.notification.domain.ProcessedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationClaimService implements NotificationClaimUseCase {

    private final ProcessedEventRepository repository;

    @Transactional
    public List<ProcessedEvent> claim(
            int batchSize
    ) {

        List<ProcessedEvent> deliveries =
                repository.claimPending(batchSize);

        for (ProcessedEvent delivery : deliveries) {
            delivery.changeStatus(DeliveryStatus.PROCESSING);
        }

        repository.saveAll(deliveries);

        return deliveries;
    }
}
