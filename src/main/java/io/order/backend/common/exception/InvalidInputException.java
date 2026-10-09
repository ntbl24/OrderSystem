package io.order.backend.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter 
public class InvalidInputException extends BaseException {
    private static final String ERROR_CODE = "INVALID_INPUT";

    public InvalidInputException(String message) {
        super(message, ERROR_CODE, HttpStatus.BAD_REQUEST);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, ERROR_CODE, HttpStatus.BAD_REQUEST, cause);
    }
}
