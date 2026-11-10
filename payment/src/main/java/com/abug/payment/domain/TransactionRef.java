package com.abug.payment.domain;

import java.util.Objects;
import java.util.UUID;

import com.abug.common.exception.AppException;
import com.abug.common.exception.ErrorCode;

public record TransactionRef(String value) {
    public TransactionRef {
        Objects.requireNonNull(value, "Transaction ref cannot be null");
        if (value.isBlank()) {
            throw new AppException(ErrorCode.INVALID_INPUT,"Transaction ref cannot be blank");
        }
    }

    public static TransactionRef generate(){
        return new TransactionRef(UUID.randomUUID().toString().replace("-", ""));
    }

    public static TransactionRef of(String value) {
        return new TransactionRef(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
