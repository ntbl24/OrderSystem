package io.order.backend.common.application.port;

import java.util.function.Supplier;

/** Executes a buyer-scoped command and replays its stored response on matching retries.
 * The command and its result must commit in the same database transaction.
 */
public interface IdempotencyPort {
    <T> T execute(String buyerId, String operation, String key, Object request,
                  Class<T> responseType, Supplier<T> action);
}
