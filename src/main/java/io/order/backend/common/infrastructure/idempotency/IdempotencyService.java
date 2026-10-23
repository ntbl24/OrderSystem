package io.order.backend.common.infrastructure.idempotency;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.function.Supplier;
import io.order.backend.common.exception.ConflictException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

@Service
public class IdempotencyService implements io.order.backend.common.application.port.IdempotencyPort {
    private final JdbcTemplate jdbc;
    private final JsonMapper json;

    public IdempotencyService(JdbcTemplate jdbc, JsonMapper json) {
        this.jdbc = jdbc;
        this.json = json;
    }

    @Transactional(timeout = 15)
    public <T> T execute(String buyerId, String operation, String key, Object request,
                         Class<T> responseType, Supplier<T> action) {
        if (buyerId == null || buyerId.isBlank() || operation == null || operation.isBlank()
                || key == null || !key.matches("[A-Za-z0-9._:-]{1,128}")) {
            throw new IllegalArgumentException("Invalid idempotency scope or key");
        }
        String hash = hash(json.writeValueAsString(request));
        jdbc.execute("SET LOCAL lock_timeout = '5s'");
        int claimed = jdbc.update("""
            INSERT INTO idempotency_records(buyer_id, operation, request_key, request_hash)
            VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING
            """, buyerId, operation, key, hash);
        if (claimed == 0) {
            return jdbc.queryForObject("""
                SELECT request_hash, response_body FROM idempotency_records
                WHERE buyer_id = ? AND operation = ? AND request_key = ?
                """, (row, number) -> {
                    if (!hash.equals(row.getString("request_hash"))) {
                        throw new ConflictException("Idempotency key was used with another request");
                    }
                    String body = row.getString("response_body");
                    if (body == null) {
                        throw new ConflictException("Request is still in progress");
                    }
                    return json.readValue(body, responseType);
                }, buyerId, operation, key);
        }
        T response = action.get();
        String body = json.writeValueAsString(response);
        if (body.getBytes(StandardCharsets.UTF_8).length > 262144) {
            throw new IllegalArgumentException("Idempotent response exceeds storage limit");
        }
        jdbc.update("""
            UPDATE idempotency_records SET response_body = ?, completed_at = CURRENT_TIMESTAMP
            WHERE buyer_id = ? AND operation = ? AND request_key = ?
            """, body, buyerId, operation, key);
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
