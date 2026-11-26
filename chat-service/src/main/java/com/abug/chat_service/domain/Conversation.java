package com.abug.chat_service.domain;


import com.abug.chat_service.domain.valueobject.ConversationId;

import java.time.Instant;
import java.util.List;

public class Conversation {
    private ConversationId id;
    private ConversationType type;
    private String participantsHash;
    private List<ParticipantInfo> participants;
    private Instant createdDate;
    private Instant modifiedDate;

    private Conversation(ConversationId id, ConversationType type, String participantsHash, List<ParticipantInfo> participants, Instant createdDate, Instant modifiedDate) {
        this.id = id;
        this.type = type;
        this.participantsHash = participantsHash;
        this.participants = participants;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static Conversation create(ConversationType type, String participantsHash, List<ParticipantInfo> participants){
        return new Conversation(ConversationId.generate(), type, participantsHash, participants, Instant.now(), Instant.now());
    }

    public static Conversation reconstitute(ConversationId id, ConversationType type, String participantsHash, List<ParticipantInfo> participants, Instant createdDate, Instant modifiedDate){
        return new Conversation(id, type, participantsHash, participants, createdDate, modifiedDate);
    }

    public ConversationId getId() {
        return id;
    }

    public ConversationType getType() {
        return type;
    }

    public String getParticipantsHash() {
        return participantsHash;
    }

    public List<ParticipantInfo> getParticipants() {
        return participants;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }
}
