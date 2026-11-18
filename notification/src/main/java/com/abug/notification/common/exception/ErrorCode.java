package com.abug.notification.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 9999: Lỗi hệ thống / Uncategorized (HTTP 500)
    UNCATEGORIZED_EXCEPTION(-9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL_SERVER_ERROR(-9999, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST(-9999, "Bad request", HttpStatus.BAD_REQUEST),

    // 1001: Lỗi dữ liệu không hợp lệ / Validation (HTTP 400)
    INVALID_KEY(-1001, "Invalid message key", HttpStatus.BAD_REQUEST),
    INVALID_INPUT(-1002, "Invalid input data", HttpStatus.BAD_REQUEST),

    // 1002: Chưa xác thực (HTTP 401)
    UNAUTHENTICATED(-1006, "Authentication required", HttpStatus.UNAUTHORIZED),

    // 1003: Không có quyền truy cập (HTTP 403)
    UNAUTHORIZED(-1007, "Access denied", HttpStatus.FORBIDDEN),

    // 1004: Lỗi không tìm thấy tài nguyên - dùng chung 1 mã 1004 (HTTP 404)
    RESOURCE_NOT_FOUND(-1008, "Resource not found", HttpStatus.NOT_FOUND),

    // 1005: Lỗi xung đột / Đã tồn tại / Concurrency - dùng chung 1 mã 1005 (HTTP 409)
    CONFLICT(-1009, "Conflicting operation", HttpStatus.CONFLICT),

    CANNOT_SEND_EMAIL(-1011, "Cannot send email", HttpStatus.BAD_REQUEST),
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public int getStatus() {
        return httpStatus.value();
    }
}
