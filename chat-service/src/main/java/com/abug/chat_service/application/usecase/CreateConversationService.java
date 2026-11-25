package com.abug.chat_service.application.usecase;

import com.abug.chat_service.application.dto.CreateConversationResult;
import com.abug.chat_service.application.port.in.CreateConversationUseCase;
import com.abug.chat_service.application.port.in.command.CreateConversationCommand;
import com.abug.chat_service.application.port.out.ConversationRepository;
import com.abug.chat_service.application.port.out.CurrentUserPort;
import com.abug.chat_service.application.port.out.ProfileUserPort;
import com.abug.chat_service.domain.Conversation;
import com.abug.chat_service.domain.ParticipantInfo;
import com.abug.chat_service.domain.UserId;
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
    private final ProfileUserPort profileUserPort;
    private final ConversationRepository conversationRepository;

    @Override
    public CreateConversationResult create(CreateConversationCommand command) {
        UUID userId = currentUserPort.currentUserId();

        ProfileUserPort.Snapshot userProfile = profileUserPort.getProfile(UserId.of(userId));
        ProfileUserPort.Snapshot participantProfile = profileUserPort.getProfile(command.participantIds().getFirst());

        List<ProfileUserPort.Snapshot> participantProfiles =  List.of(userProfile, participantProfile);

        List<String> userIds = participantProfiles.stream()
                .map(ProfileUserPort.Snapshot::userId)
                .sorted()
                .toList();

        String userIdHash = generateParticipantHash(userIds);

        Conversation conversation = conversationRepository.findByParticipantsHash(userIdHash)
                .orElseGet(() -> {
                    List<ParticipantInfo> participantInfos = participantProfiles.stream().map(profile ->
                            ParticipantInfo.create(UserId.of(profile.userId()), profile.username(), profile.firstName(), profile.lastName(), profile.avatar())).toList();

                    return Conversation.create(command.type(), userIdHash, participantInfos);
                });

        conversationRepository.save(conversation);

        return new CreateConversationResult(UserId.of(userId), conversation);
    }

    private String generateParticipantHash(List<String> ids){
        StringJoiner stringJoiner = new StringJoiner("_");
        ids.forEach(stringJoiner::add);
        return stringJoiner.toString();
    }
}
