package com.abug.payment.common.exception;


public class AppException extends RuntimeException {
    private ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode != null ? errorCode.getMessage() : ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        this.errorCode = errorCode != null ? errorCode : ErrorCode.UNCATEGORIZED_EXCEPTION;
    }

    public AppException(ErrorCode errorCode, String message) {
        super(message != null && !message.isBlank() ? message : (errorCode != null ? errorCode.getMessage() : ""));
        this.errorCode = errorCode != null ? errorCode : ErrorCode.UNCATEGORIZED_EXCEPTION;
    }

    public AppException(ErrorCode errorCode, Throwable cause) {
        super(errorCode != null ? errorCode.getMessage() : "", cause);
        this.errorCode = errorCode != null ? errorCode : ErrorCode.UNCATEGORIZED_EXCEPTION;
    }

    public AppException(ErrorCode errorCode, String message, Throwable cause) {
        super(message != null && !message.isBlank() ? message : (errorCode != null ? errorCode.getMessage() : ""), cause);
        this.errorCode = errorCode != null ? errorCode : ErrorCode.UNCATEGORIZED_EXCEPTION;
    }

    public AppException(String message) {
        super(message);
        this.errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
    }

    public AppException(String message, String code, int status) {
        super(message);
        ErrorCode resolved = null;
        try {
            resolved = ErrorCode.valueOf(code);
        } catch (Exception ignored) {}
        this.errorCode = resolved != null ? resolved : ErrorCode.UNCATEGORIZED_EXCEPTION;
    }

    public AppException(String message, String code, int status, Throwable cause) {
        super(message, cause);
        ErrorCode resolved = null;
        try {
            resolved = ErrorCode.valueOf(code);
        } catch (Exception ignored) {}
        this.errorCode = resolved != null ? resolved : ErrorCode.UNCATEGORIZED_EXCEPTION;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getCode() {
        return errorCode != null ? errorCode.name() : "UNCATEGORIZED_EXCEPTION";
    }

    public int getStatus() {
        return errorCode != null ? errorCode.getStatus() : 500;
    }
}
