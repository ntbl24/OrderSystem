package com.abug.cart.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 9999: Lỗi hệ thống / Uncategorized (HTTP 500)
    UNCATEGORIZED_EXCEPTION(-9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL_SERVER_ERROR(-9999, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST(-9999, "Bad request", HttpStatus.BAD_REQUEST),

    // 1001: Lỗi dữ liệu không hợp lệ / Validation (HTTP 400)
    INVALID_KEY(-1001, "Invalid message key", HttpStatus.BAD_REQUEST),
    INVALID_INPUT(-1002, "Invalid input data", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(-1004, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(-1005, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(-1012, "Invalid email address", HttpStatus.BAD_REQUEST),
    EMAIL_IS_REQUIRED(-1013, "Email is required", HttpStatus.BAD_REQUEST),


    // 1002: Chưa xác thực (HTTP 401)
    UNAUTHENTICATED(-1006, "Authentication required", HttpStatus.UNAUTHORIZED),

    // 1003: Không có quyền truy cập (HTTP 403)
    UNAUTHORIZED(-1007, "Access denied", HttpStatus.FORBIDDEN),

    // 1004: Lỗi không tìm thấy tài nguyên - dùng chung 1 mã 1004 (HTTP 404)
    RESOURCE_NOT_FOUND(-1008, "Resource not found", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND(-1008, "Product not found", HttpStatus.NOT_FOUND),
    CART_NOT_FOUND(-1008, "Cart not found", HttpStatus.NOT_FOUND),
    ORDER_NOT_FOUND(-1008, "Order not found", HttpStatus.NOT_FOUND),
    PAYMENT_NOT_FOUND(-1008, "Payment not found", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(-1008, "User not found", HttpStatus.NOT_FOUND),
    SHIPMENT_NOT_FOUND(-1008, "Shipment not found", HttpStatus.NOT_FOUND),

    // 1005: Lỗi xung đột / Đã tồn tại / Concurrency - dùng chung 1 mã 1005 (HTTP 409)
    CONFLICT(-1009, "Conflicting operation", HttpStatus.CONFLICT),
    PRODUCT_EXISTED(-1010, "Product already existed", HttpStatus.CONFLICT),
    CART_EXISTED(-1010, "Cart already exists", HttpStatus.CONFLICT),
    USER_EXISTED(-1010, "User already exists", HttpStatus.CONFLICT),
    IDEMPOTENCY_CONFLICT(-1011, "Conflicting or concurrent operation; retry with the same idempotency key", HttpStatus.CONFLICT)
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
