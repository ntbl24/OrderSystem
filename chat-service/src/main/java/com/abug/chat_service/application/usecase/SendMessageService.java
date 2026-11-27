package com.abug.chat_service.application.usecase;

import com.abug.chat_service.application.port.in.SendMessageUseCase;
import com.abug.chat_service.application.port.in.command.SendMessageCommand;
import com.abug.chat_service.application.port.out.CurrentUserPort;
import com.abug.chat_service.application.port.out.WebSocketSessionRepository;
import com.abug.chat_service.domain.WebSocketSession;
import com.abug.chat_service.domain.valueobject.UserId;
import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SendMessageService implements SendMessageUseCase {
    private final CurrentUserPort currentUserPort;
    private final SocketIOServer server;
    private final WebSocketSessionRepository webSocketSessionRepository;
    private final ObjectMapper objectMapper;

    public void sendMessageToUserId(SendMessageCommand command){
        Map<String, WebSocketSession> webSocketSessions = webSocketSessionRepository
                .findAllByUserIdIn(command.userIds()).stream()
                .collect(Collectors.toMap(
                        WebSocketSession::getSocketSessionId,
                        Function.identity()
                ));

        UserId userId = UserId.of(currentUserPort.currentUserId());

        server.getAllClients().forEach(client -> {
            WebSocketSession webSocketSession = webSocketSessions.get(client.getSessionId().toString());
            if(Objects.nonNull(webSocketSession)){
                String message = null;
                try{
                    command.message().setMe(webSocketSession.getUserId().equals(userId));
                    message = objectMapper.writeValueAsString(command.message());
                    client.sendEvent("message", message);
                } catch (JsonProcessingException e){
                    throw new RuntimeException(e);
                }

            }
        });
    }
}
