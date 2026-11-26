package com.abug.chat_service.domain.valueobject;

import java.util.UUID;

public record WebSocketSessionId(UUID value) {
    public static WebSocketSessionId generate (){ return new WebSocketSessionId(UUID.randomUUID());}

    public static WebSocketSessionId of(UUID value){return new WebSocketSessionId(value);}

    public static WebSocketSessionId of(String value){ return new WebSocketSessionId(UUID.fromString(value));}

    public String toString(){ return value.toString();}
}
