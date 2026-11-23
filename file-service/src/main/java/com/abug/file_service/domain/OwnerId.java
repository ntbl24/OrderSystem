package com.abug.file_service.domain;

import java.util.UUID;

public record OwnerId(UUID value) {
    public static OwnerId generate() {
        return new OwnerId(UUID.randomUUID());
    }

    public static OwnerId of(String value) {
        return new OwnerId(UUID.fromString(value));
    }

    public static OwnerId of(UUID value) {
        return new OwnerId(value);
    }

    public String toString() {
        return value.toString();
    }
}
