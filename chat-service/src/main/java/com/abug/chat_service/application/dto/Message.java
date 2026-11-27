package com.abug.chat_service.application.dto;


import com.abug.chat_service.domain.ParticipantInfo;
import lombok.*;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message{
    private String conversationId;
    private boolean me;
    private String message;
    private ParticipantInfo sender;
    private Instant createdDate;
}
