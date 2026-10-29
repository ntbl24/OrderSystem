package io.order.backend.common.infrastructure.messaging;

import io.order.backend.common.application.port.OutboxPort;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxService implements OutboxPort {
    private final JdbcTemplate jdbc;

    public OutboxService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void enqueue(String eventType, String aggregateId, String payload) {
        jdbc.update("""
            INSERT INTO outbox_events(id, event_type, aggregate_id, payload)
            VALUES (?, ?, ?, ?)
            """, UUID.randomUUID().toString(), eventType, aggregateId, payload);
    }
}
