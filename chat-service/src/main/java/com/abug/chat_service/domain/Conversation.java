package com.abug.chat_service.domain;


import java.util.List;

public class Conversation {
    private ConversationId id;
    private ConversationType type;
    private String participantsHash;
    private List<ParticipantInfo> participants;

    public Conversation(ConversationId id, ConversationType type, String participantsHash, List<ParticipantInfo> participants) {
        this.id = id;
        this.type = type;
        this.participantsHash = participantsHash;
        this.participants = participants;
    }
}
