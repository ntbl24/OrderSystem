package io.order.backend.common.infrastructure.idempotency;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.function.Supplier;

import io.order.backend.common.application.port.IdempotencyPort;
import io.order.backend.common.domain.idempotency.IdempotencyRecord;
import io.order.backend.common.domain.idempotency.IdempotencyScope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

@Service
public class IdempotencyService implements IdempotencyPort {
    private final JdbcTemplate jdbc;
    private final JsonMapper json;

    public IdempotencyService(JdbcTemplate jdbc, JsonMapper json) {
        this.jdbc = jdbc;
        this.json = json;
    }

    @Transactional(timeout = 15)
    public <T> T execute(String buyerId, String operation, String key, Object request,
                         Class<T> responseType, Supplier<T> action) {
        IdempotencyScope scope = new IdempotencyScope(buyerId, operation, key);
        String hash = hash(json.writeValueAsString(request));
        jdbc.execute("SET LOCAL lock_timeout = '5s'");
        int claimed = jdbc.update("""
            INSERT INTO idempotency_records(buyer_id, operation, request_key, request_hash)
            VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING
            """, scope.buyerId(), scope.operation(), scope.key(), hash);
        if (claimed == 0) {
            return jdbc.queryForObject("""
                SELECT request_hash, response_body FROM idempotency_records
                WHERE buyer_id = ? AND operation = ? AND request_key = ?
                """, (row, number) -> {
                    IdempotencyRecord record = IdempotencyRecord.reconstitute(
                        row.getString("request_hash"), row.getString("response_body"));
                    return json.readValue(record.replay(hash), responseType);
                }, scope.buyerId(), scope.operation(), scope.key());
        }
        T response = action.get();
        String body = json.writeValueAsString(response);
        IdempotencyRecord record = IdempotencyRecord.claim(hash);
        record.complete(body);
        jdbc.update("""
            UPDATE idempotency_records SET response_body = ?, completed_at = CURRENT_TIMESTAMP
            WHERE buyer_id = ? AND operation = ? AND request_key = ?
            """, body, scope.buyerId(), scope.operation(), scope.key());
        return response;
    }

    private static String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
