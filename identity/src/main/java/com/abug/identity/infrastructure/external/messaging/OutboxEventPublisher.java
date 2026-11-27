package com.abug.identity.infrastructure.external.messaging;

import com.abug.event.dto.EventMetadata;
import com.abug.event.dto.IntegrationEvent;
import com.abug.identity.application.port.out.EventPublisher;
import com.abug.identity.application.port.out.OutboxRepository;
import com.abug.identity.domain.EventId;
import com.abug.identity.domain.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher implements EventPublisher {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(IntegrationEvent event, EventMetadata metadata) {
        try{
            OutboxEvent outboxEvent = OutboxEvent.create(
                    EventId.of(event.eventId()),
                    UUID.fromString(metadata.aggregateId()),
                    metadata.aggregateType(),
                    event.eventType(),
                    metadata.topic(),
                    objectMapper.writeValueAsString(event),
                    false
            );

            outboxRepository.save(outboxEvent);
        } catch (Exception e){
            throw new IllegalStateException("Cannot create outbox event", e);
        }
    }
}
