package com.abug.chat_service.domain;

import java.util.UUID;

public record MessageId(UUID value) {
    public static MessageId generate (){ return new MessageId(UUID.randomUUID());}

    public static MessageId of(UUID value){return new MessageId(value);}

    public static MessageId of(String value){ return new MessageId(UUID.fromString(value));}

    public String toString(){ return value.toString();}
}
