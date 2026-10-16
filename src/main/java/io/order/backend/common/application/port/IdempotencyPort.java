package io.order.backend.common.application.port;

import java.util.function.Supplier;

public interface IdempotencyPort {
    <T> T execute(String buyerId, String operation, String key, Object request,
                  Class<T> responseType, Supplier<T> action);
}
