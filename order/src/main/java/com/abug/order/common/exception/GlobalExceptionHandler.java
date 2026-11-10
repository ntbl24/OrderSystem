package com.abug.order.common.exception;

import com.abug.order.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode() != null ? exception.getErrorCode() : ErrorCode.UNCATEGORIZED_EXCEPTION;
        return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponse.error(
                errorCode.getCode(),
                exception.getMessage() != null ? exception.getMessage() : errorCode.getMessage()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> validation(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError() != null ? exception.getFieldError().getDefaultMessage() : null;
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        String message = null;

        if (enumKey != null) {
            try {
                errorCode = ErrorCode.valueOf(enumKey);
                message = errorCode.getMessage();
            } catch (IllegalArgumentException e) {
                errorCode = ErrorCode.INVALID_INPUT;
                message = enumKey;
            }
        }
        if (message == null) {
            message = errorCode.getMessage();
        }

        return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponse.error(
                errorCode.getCode(),
                message
        ));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MissingRequestHeaderException.class,
            HandlerMethodValidationException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiResponse<Void>> invalid(Exception exception) {
        return ResponseEntity.badRequest().body(ApiResponse.error(
                ErrorCode.INVALID_INPUT.getCode(),
                exception.getMessage() != null ? exception.getMessage() : ErrorCode.INVALID_INPUT.getMessage()
        ));
    }

    @ExceptionHandler({DataIntegrityViolationException.class, ObjectOptimisticLockingFailureException.class,
            PessimisticLockingFailureException.class})
    public ResponseEntity<ApiResponse<Void>> conflict(Exception exception) {
        return ResponseEntity.status(ErrorCode.IDEMPOTENCY_CONFLICT.getHttpStatus()).body(ApiResponse.error(
                ErrorCode.IDEMPOTENCY_CONFLICT.getCode(),
                "Conflicting or concurrent operation; retry with the same idempotency key"
        ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> forbidden(AccessDeniedException exception) {
        return ResponseEntity.status(ErrorCode.UNAUTHORIZED.getHttpStatus()).body(ApiResponse.error(
                ErrorCode.UNAUTHORIZED.getCode(),
                ErrorCode.UNAUTHORIZED.getMessage()
        ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> unauthenticated(AuthenticationException exception) {
        return ResponseEntity.status(ErrorCode.UNAUTHENTICATED.getHttpStatus()).body(ApiResponse.error(
                ErrorCode.UNAUTHENTICATED.getCode(),
                ErrorCode.UNAUTHENTICATED.getMessage()
        ));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> illegalState(IllegalStateException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(
                ErrorCode.INVALID_INPUT.getCode(),
                exception.getMessage() != null ? exception.getMessage() : ErrorCode.INVALID_INPUT.getMessage()
        ));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> runtime(RuntimeException exception) {
        log.error("Unhandled runtime exception: {}", exception.getMessage(), exception);
        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getHttpStatus()).body(ApiResponse.error(
                ErrorCode.UNCATEGORIZED_EXCEPTION.getCode(),
                ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage()
        ));
    }
}
