package com.abug.chat_service.application.usecase;

import com.abug.chat_service.application.port.in.CreateSessionUseCase;
import com.abug.chat_service.application.port.in.command.CreateWebSocketSessionCommand;
import com.abug.chat_service.application.port.out.WebSocketSessionRepository;
import com.abug.chat_service.domain.WebSocketSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateSessionService implements CreateSessionUseCase {
    private final WebSocketSessionRepository repository;

    @Override
    @Transactional
    public WebSocketSession create(CreateWebSocketSessionCommand command) {
        WebSocketSession webSocketSession = WebSocketSession.create(command.sessionId(), command.userId());
        return repository.save(webSocketSession);
    }
}
