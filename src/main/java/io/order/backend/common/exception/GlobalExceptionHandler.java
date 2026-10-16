package io.order.backend.common.exception;

import io.order.backend.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
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
    @ExceptionHandler(BaseException.class)
    ResponseEntity<ApiResponse<Void>> business(BaseException exception) {
        return ResponseEntity.status(exception.getStatus()).body(ApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class,
        MissingRequestHeaderException.class, HandlerMethodValidationException.class, IllegalArgumentException.class})
    ResponseEntity<ApiResponse<Void>> invalid(Exception exception) {
        return ResponseEntity.badRequest().body(ApiResponse.error("Invalid request"));
    }

    @ExceptionHandler({DataIntegrityViolationException.class, ObjectOptimisticLockingFailureException.class,
        PessimisticLockingFailureException.class})
    ResponseEntity<ApiResponse<Void>> conflict(Exception exception) {
        return ResponseEntity.status(409).body(ApiResponse.error("Conflicting or concurrent operation; retry with the same idempotency key"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiResponse<Void>> forbidden(AccessDeniedException exception) {
        return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
    }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ApiResponse<Void>> unauthenticated(AuthenticationException exception) {
        return ResponseEntity.status(401).body(ApiResponse.error("Authentication required"));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse<Void>> unexpected(Exception exception) {
        log.error("Unhandled request failure ({})", exception.getClass().getSimpleName());
        return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error"));
    }
}
