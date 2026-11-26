package com.abug.chat_service.application.usecase;

import com.abug.chat_service.application.port.in.DeleteSessionUseCase;
import com.abug.chat_service.application.port.out.WebSocketSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteSessionService implements DeleteSessionUseCase {
    private final WebSocketSessionRepository repository;
    @Override
    public void deleteSession(String sessionId) {
        repository.deleteBySocketSessionId(sessionId);
    }
}
