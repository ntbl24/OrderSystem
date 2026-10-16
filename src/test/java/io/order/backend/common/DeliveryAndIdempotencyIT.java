package io.order.backend.common;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import io.order.backend.common.exception.ConflictException;
import io.order.backend.common.idempotency.IdempotencyService;
import io.order.backend.common.messaging.OutboxDispatcher;
import io.order.backend.common.messaging.OutboxHandler;
import io.order.backend.common.messaging.OutboxService;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.postgresql.PostgreSQLContainer;
import tools.jackson.databind.json.JsonMapper;

@SpringJUnitConfig(DeliveryAndIdempotencyIT.Config.class)
class DeliveryAndIdempotencyIT {
    @Autowired JdbcTemplate jdbc;
    @Autowired IdempotencyService idempotency;
    @Autowired OutboxService outbox;
    @Autowired OutboxDispatcher dispatcher;
    @Autowired PlatformTransactionManager manager;

    @BeforeEach
    void reset() {
        jdbc.execute("TRUNCATE idempotency_records, outbox_events, inbox_events, test_effects");
    }

    @Test
    void execute_concurrentSameKey_shouldApplyOnceAndReplayOriginalResult() throws Exception {
        Callable<Result> request = () -> idempotency.execute("buyer", "checkout", "same-key", "payload", Result.class, () -> {
            jdbc.update("INSERT INTO test_effects(id) VALUES ('checkout')");
            return new Result("original");
        });
        try (var executor = Executors.newFixedThreadPool(2)) {
            var futures = executor.invokeAll(List.of(request, request));
            assertThat(futures.get(0).get()).isEqualTo(new Result("original"));
            assertThat(futures.get(1).get()).isEqualTo(new Result("original"));
        }
        assertThat(jdbc.queryForObject("SELECT count(*) FROM test_effects", Integer.class)).isEqualTo(1);
    }

    @Test
    void execute_completedRequest_shouldNeverRunBusinessActionAgain() {
        idempotency.execute("buyer", "checkout", "key", "one", Result.class, () -> new Result("original"));
        assertThat(idempotency.execute("buyer", "checkout", "key", "one", Result.class, () -> {
            throw new AssertionError("Replay must not invoke the business action");
        })).isEqualTo(new Result("original"));
    }

    @Test
    void execute_conflictingPayload_shouldRejectWithoutApplyingAgain() {
        idempotency.execute("buyer", "checkout", "key", "one", Result.class, () -> new Result("one"));
        assertThatThrownBy(() -> idempotency.execute("buyer", "checkout", "key", "two", Result.class,
            () -> new Result("two"))).isInstanceOf(ConflictException.class);
    }

    @Test
    void execute_differentBuyer_shouldNotReplayAnotherBuyersResult() {
        idempotency.execute("buyer-a", "checkout", "key", "same", Result.class, () -> new Result("a"));
        assertThat(idempotency.execute("buyer-b", "checkout", "key", "same", Result.class,
            () -> new Result("b"))).isEqualTo(new Result("b"));
    }

    @Test
    void execute_failedBusinessWrite_shouldRollbackClaimAndMutation() {
        assertThatThrownBy(() -> idempotency.execute("buyer", "checkout", "key", "same", Result.class, () -> {
            jdbc.update("INSERT INTO test_effects(id) VALUES ('rollback')");
            throw new IllegalStateException("simulated failure");
        })).isInstanceOf(IllegalStateException.class);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM idempotency_records", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM test_effects", Integer.class)).isZero();
    }

    @Test
    void dispatch_restartAndDuplicateDispatch_shouldPersistOneConsumerEffect() {
        new TransactionTemplate(manager).executeWithoutResult(status -> outbox.enqueue("TEST", "order", "payload"));
        dispatcher.dispatch();
        dispatcher.dispatch();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM test_effects", Integer.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM inbox_events", Integer.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM outbox_events WHERE delivered_at IS NOT NULL", Integer.class)).isEqualTo(1);
    }

    @Test
    void dispatch_failedConsumer_shouldRollbackInboxAndRetryDurably() {
        new TransactionTemplate(manager).executeWithoutResult(status -> outbox.enqueue("TEST", "fail", "payload"));
        dispatcher.dispatch();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM inbox_events", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM test_effects", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT attempts FROM outbox_events", Integer.class)).isEqualTo(1);
        jdbc.update("UPDATE outbox_events SET aggregate_id = 'recovered', available_at = CURRENT_TIMESTAMP");
        dispatcher.dispatch();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM inbox_events", Integer.class)).isEqualTo(1);
    }

    public record Result(String value) {}

    @Configuration(proxyBeanMethods = false)
    @EnableTransactionManagement
    static class Config {
        private static PostgreSQLContainer container;

        @Bean
        DataSource dataSource() {
            String url = System.getProperty("test.db.url");
            String username = System.getProperty("test.db.username", "orders_test");
            String password = System.getProperty("test.db.password", "");
            if (url == null) {
                container = new PostgreSQLContainer("postgres:17-alpine");
                container.start();
                url = container.getJdbcUrl();
                username = container.getUsername();
                password = container.getPassword();
            }
            var admin = new DriverManagerDataSource(url, username, password);
            new JdbcTemplate(admin).execute("DROP SCHEMA IF EXISTS shared_delivery_it CASCADE");
            new JdbcTemplate(admin).execute("CREATE SCHEMA shared_delivery_it");
            var source = new DriverManagerDataSource(url + (url.contains("?") ? "&" : "?") + "currentSchema=shared_delivery_it", username, password);
            new ResourceDatabasePopulator(new ClassPathResource("db/migration/V3__delivery_and_idempotency.sql")).execute(source);
            new JdbcTemplate(source).execute("CREATE TABLE test_effects(id varchar(255) PRIMARY KEY)");
            return source;
        }

        @Bean JdbcTemplate jdbc(DataSource source) { return new JdbcTemplate(source); }
        @Bean PlatformTransactionManager transactionManager(DataSource source) { return new DataSourceTransactionManager(source); }
        @Bean JsonMapper json() { return JsonMapper.builder().build(); }
        @Bean IdempotencyService idempotency(JdbcTemplate jdbc, JsonMapper json) { return new IdempotencyService(jdbc, json); }
        @Bean OutboxService outbox(JdbcTemplate jdbc) { return new OutboxService(jdbc); }
        @Bean OutboxHandler handler(JdbcTemplate jdbc) {
            return new OutboxHandler() {
                public String eventType() { return "TEST"; }
                public void handle(String eventId, String aggregateId, String payload) {
                    jdbc.update("INSERT INTO test_effects(id) VALUES (?)", eventId);
                    if (aggregateId.equals("fail")) { throw new IllegalStateException("simulated consumer failure"); }
                }
            };
        }
        @Bean OutboxDispatcher dispatcher(JdbcTemplate jdbc, PlatformTransactionManager manager, OutboxHandler handler) {
            return new OutboxDispatcher(jdbc, manager, List.of(handler));
        }
    }
}
