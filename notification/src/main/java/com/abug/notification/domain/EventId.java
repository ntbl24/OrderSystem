package com.abug.notification.domain;

import com.abug.notification.common.exception.AppException;
import com.abug.notification.common.exception.ErrorCode;

import java.util.UUID;

public record EventId (UUID value){
    public EventId{
        if(value == null){
            throw new AppException(ErrorCode.INVALID_INPUT);
        }
    }

    public static EventId generate() {
        return new EventId(UUID.randomUUID());
    }

    public static EventId of(String value) {
        return new EventId(UUID.fromString(value));
    }

    public static EventId of(UUID value) {
        return new EventId(value);
    }

    public String toString() {
        return value.toString();
    }
}
