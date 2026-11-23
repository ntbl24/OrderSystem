package com.abug.identity.domain;

import java.util.UUID;

public record EventId(UUID value){
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
