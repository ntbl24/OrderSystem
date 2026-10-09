package io.order.backend.common.exception;

import lombok.Getter;

@Getter 
public class InternalException extends BaseException {

    public InternalException(String message) {
        super(message);
    }

    public InternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
