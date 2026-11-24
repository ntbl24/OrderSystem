package com.abug.chat_service.application.usecase;

import com.abug.chat_service.application.port.in.CreateConversationUseCase;
import com.abug.chat_service.application.port.out.CurrentUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateConversationService implements CreateConversationUseCase {
    private final CurrentUserPort currentUserPort;

    @Override
    public Object create() {
        UUID userId = currentUserPort.currentUserId();

        return null;
    }

    private String generateParticipantHash(List<String> ids){
        StringJoiner stringJoiner = new StringJoiner("_");
        ids.forEach(stringJoiner::add);
        return stringJoiner.toString();
    }
}
