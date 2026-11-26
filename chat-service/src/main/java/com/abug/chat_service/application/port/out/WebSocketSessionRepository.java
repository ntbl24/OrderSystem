package com.abug.chat_service.application.port.out;

import com.abug.chat_service.domain.WebSocketSession;
import com.abug.chat_service.domain.valueobject.UserId;

import java.util.List;

public interface WebSocketSessionRepository {
    WebSocketSession save(WebSocketSession session);
    void deleteBySocketSessionId(String sessionId);
    List<WebSocketSession> findAllByUserIdIn(List<UserId> userIds);
}
