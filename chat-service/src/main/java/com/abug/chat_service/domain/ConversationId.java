package com.abug.chat_service.domain;

import java.util.UUID;

public record ConversationId(UUID value) {
    public static ConversationId generate (){ return new ConversationId(UUID.randomUUID());}

    public static ConversationId of(UUID value){return new ConversationId(value);}

    public static ConversationId of(String value){ return new ConversationId(UUID.fromString(value));}

    public String toString(){ return value.toString();}
}
