package io.order.backend.common.exception;

import lombok.Getter;

@Getter 
public class InvalidInputException extends BaseException {
    private static final String ERROR_CODE = "INVALID_INPUT";

    public InvalidInputException(String message) {
        super(message, ERROR_CODE, 400);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, ERROR_CODE, 400, cause);
    }
}
