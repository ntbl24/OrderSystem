package com.abug.identity.infrastructure.external.messaging;

import com.abug.event.dto.NotificationEvent;
import com.abug.identity.application.port.out.OutboxRepository;
import com.abug.identity.domain.OutboxEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 3000)
    public void publishPendingEvents(){
        List<OutboxEvent> events = outboxRepository.findTop100ByPublishedFalseOrderByCreatedAtAsc();

        for (OutboxEvent event: events){
            try{
                kafkaTemplate.send(event.getTopic(), event.getAggregateId().toString(), event.getPayload()).get();

                event.markPublished();
                outboxRepository.save(event);
            } catch (Exception e){
                log.error(
                    "Failed to publish event: {}",
                    event.getId(),
                    e
                );
            }
        }
    }
}
