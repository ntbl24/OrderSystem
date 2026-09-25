package io.order.backend.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends BaseException{
    private static final String ERROR_CODE = "RESOURCE_NOT_FOUND";

    public ResourceNotFoundException(String message) {
        super(message, ERROR_CODE, HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, ERROR_CODE, HttpStatus.NOT_FOUND, cause);
    }
    
}
