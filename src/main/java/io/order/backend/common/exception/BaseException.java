package io.order.backend.common.exception;

import lombok.Getter;

@Getter 
public class BaseException extends RuntimeException{
    private final String code;
    private final int status;

    public BaseException(String message) {
        super(message);
        this.code = "INTERNAL_SERVER_ERROR";
        this.status = 500;
    }

    public BaseException(String message, String code, int status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.code = "INTERNAL_SERVER_ERROR";
        this.status = 500;
    }
    
    public BaseException(String message, String code, int status, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.status = status;
    }
    
}
