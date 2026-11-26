package com.abug.chat_service.domain;

import com.abug.chat_service.domain.valueobject.UserId;
import com.abug.chat_service.domain.valueobject.WebSocketSessionId;

public class WebSocketSession {
    private WebSocketSessionId id;
    private String socketSessionId;
    private UserId userId;

    private WebSocketSession(WebSocketSessionId id, String socketSessionId, UserId userId) {
        this.id = id;
        this.socketSessionId = socketSessionId;
        this.userId = userId;
    }

    public static WebSocketSession create(String socketSessionId, UserId userId){
        return new WebSocketSession(WebSocketSessionId.generate(), socketSessionId, userId);
    }

    public WebSocketSessionId getId() {
        return id;
    }

    public String getSocketSessionId() {
        return socketSessionId;
    }

    public UserId getUserId() {
        return userId;
    }
}
