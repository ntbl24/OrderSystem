package io.order.backend.common.messaging;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.workers.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxDispatcher {
    private final JdbcTemplate jdbc;
    private final TransactionTemplate transaction;
    private final Map<String, OutboxHandler> handlers;

    public OutboxDispatcher(JdbcTemplate jdbc, PlatformTransactionManager manager, List<OutboxHandler> handlers) {
        this.jdbc = jdbc;
        this.transaction = new TransactionTemplate(manager);
        this.transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.transaction.setTimeout(20);
        this.handlers = handlers.stream().collect(Collectors.toUnmodifiableMap(OutboxHandler::eventType, Function.identity()));
    }

    @Scheduled(fixedDelayString = "${app.outbox.poll-delay-ms:1000}")
    public void dispatch() {
        List<String> ids = jdbc.queryForList("""
            SELECT id FROM outbox_events WHERE delivered_at IS NULL AND attempts < 10
            AND available_at <= CURRENT_TIMESTAMP ORDER BY created_at, id LIMIT 50
            """, String.class);
        for (String id : ids) {
            try {
                transaction.executeWithoutResult(status -> deliver(id));
            } catch (Exception exception) {
                transaction.executeWithoutResult(status -> jdbc.update("""
                    UPDATE outbox_events SET attempts = attempts + 1,
                        available_at = CURRENT_TIMESTAMP + LEAST(300, power(2, attempts + 1)) * INTERVAL '1 second',
                        last_error = ? WHERE id = ? AND delivered_at IS NULL
                    """, exception.getClass().getSimpleName(), id));
                log.warn("Outbox event {} failed ({})", id, exception.getClass().getSimpleName());
            }
        }
    }

    private void deliver(String id) {
        var events = jdbc.query("""
            SELECT id, event_type, aggregate_id, payload FROM outbox_events
            WHERE id = ? AND delivered_at IS NULL AND attempts < 10 AND available_at <= CURRENT_TIMESTAMP
            FOR UPDATE SKIP LOCKED
            """, (row, number) -> new Event(row.getString(1), row.getString(2), row.getString(3), row.getString(4)), id);
        if (events.isEmpty()) {
            return;
        }
        Event event = events.getFirst();
        OutboxHandler handler = handlers.get(event.type());
        if (handler == null) {
            throw new IllegalStateException("No registered consumer for " + event.type());
        }
        int claimed = jdbc.update("""
            INSERT INTO inbox_events(consumer, event_id) VALUES (?, ?) ON CONFLICT DO NOTHING
            """, event.type(), event.id());
        if (claimed == 1) {
            handler.handle(event.id(), event.aggregateId(), event.payload());
        }
        jdbc.update("UPDATE outbox_events SET delivered_at = CURRENT_TIMESTAMP, last_error = NULL WHERE id = ?", id);
    }

    private record Event(String id, String type, String aggregateId, String payload) {}
}
