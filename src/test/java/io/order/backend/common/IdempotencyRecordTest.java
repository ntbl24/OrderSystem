package io.order.backend.common;

import io.order.backend.common.domain.idempotency.IdempotencyRecord;
import io.order.backend.common.domain.idempotency.IdempotencyScope;
import io.order.backend.common.exception.ConflictException;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class IdempotencyRecordTest {
    @Test
    void complete_matchingRequest_shouldReplayOriginalBody() {
        var record = IdempotencyRecord.claim("hash");
        record.complete("{\"value\":\"original\"}");
        assertThat(record.replay("hash")).isEqualTo("{\"value\":\"original\"}");
    }

    @Test
    void replay_inProgressRequest_shouldRejectRetry() {
        assertThatThrownBy(() -> IdempotencyRecord.claim("hash").replay("hash"))
            .isInstanceOf(ConflictException.class).hasMessage("Request is still in progress");
    }

    @Test
    void replay_conflictingRequest_shouldRejectBeforeCheckingCompletion() {
        assertThatThrownBy(() -> IdempotencyRecord.claim("hash").replay("other"))
            .isInstanceOf(ConflictException.class).hasMessage("Idempotency key was used with another request");
    }

    @Test
    void reconstitute_completedRequest_shouldReplayStoredBody() {
        assertThat(IdempotencyRecord.reconstitute("hash", "null").replay("hash")).isEqualTo("null");
    }

    @Test
    void complete_responseAtByteLimit_shouldAccept() {
        var record = IdempotencyRecord.claim("hash");
        String body = "é".repeat(131072);
        record.complete(body);
        assertThat(record.replay("hash")).isEqualTo(body);
    }

    @Test
    void complete_oversizedUtf8Response_shouldNotChangeRecord() {
        var record = IdempotencyRecord.claim("hash");
        assertThatThrownBy(() -> record.complete("é".repeat(131073)))
            .isInstanceOf(IllegalArgumentException.class).hasMessage("Idempotent response exceeds storage limit");
        assertThatThrownBy(() -> record.replay("hash")).isInstanceOf(ConflictException.class);
    }

    @Test
    void complete_alreadyCompleted_shouldPreserveOriginalResponse() {
        var record = IdempotencyRecord.reconstitute("hash", "original");
        assertThatThrownBy(() -> record.complete("replacement")).isInstanceOf(IllegalStateException.class);
        assertThat(record.replay("hash")).isEqualTo("original");
    }

    @Test
    void scope_validKeys_shouldPreserveBuyerAndOperation() {
        var scope = new IdempotencyScope("buyer", "checkout", "a-Z_0.:" + "x".repeat(121));
        assertThat(scope.buyerId()).isEqualTo("buyer");
        assertThat(scope.operation()).isEqualTo("checkout");
        assertThat(scope.key()).hasSize(128);
    }

    @Test
    void scope_invalidIdentityOrKey_shouldReject() {
        assertThatThrownBy(() -> new IdempotencyScope(null, "checkout", "key")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new IdempotencyScope(" ", "checkout", "key")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new IdempotencyScope("buyer", null, "key")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new IdempotencyScope("buyer", " ", "key")).isInstanceOf(IllegalArgumentException.class);
        for (String key : new String[] {null, "", "invalid key", "x".repeat(129)}) {
            assertThatThrownBy(() -> new IdempotencyScope("buyer", "checkout", key)).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
