package com.abug.chat_service.application.usecase;

import com.abug.chat_service.application.port.out.WebSocketSessionRepository;
import com.abug.chat_service.domain.WebSocketSession;
import com.abug.chat_service.domain.valueobject.UserId;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SendMessageService {
    private final SocketIOServer server;
    private final WebSocketSessionRepository webSocketSessionRepository;

    public void sendMessageToUserId(List<UserId> userIds, String message){
        Set<String> webSocketSessions = webSocketSessionRepository
                .findAllByUserIdIn(userIds).stream().map(WebSocketSession::getSocketSessionId)
                .collect(Collectors.toSet());

        server.getAllClients().forEach(client -> {
            if(webSocketSessions.contains(client.getSessionId().toString())){
                client.sendEvent("message", message);
            }
        });
    }
}
