package com.abug.file_service.domain;

import java.util.UUID;

public record FileId(UUID value) {
    public static FileId generate() {
        return new FileId(UUID.randomUUID());
    }

    public static FileId of(String value) {
        return new FileId(UUID.fromString(value));
    }

    public static FileId of(UUID value) {
        return new FileId(value);
    }

    public String toString() {
        return value.toString();
    }
}
