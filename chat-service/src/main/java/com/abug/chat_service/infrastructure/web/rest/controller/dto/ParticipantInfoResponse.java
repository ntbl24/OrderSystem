package com.abug.chat_service.infrastructure.web.rest.controller.dto;

import com.abug.chat_service.domain.ParticipantInfo;

public record ParticipantInfoResponse(
        String userId,
        String username,
        String firstName,
        String lastName,
        String avatar
) {
    public static ParticipantInfoResponse from(ParticipantInfo participantInfo){
        return new ParticipantInfoResponse(
                participantInfo.getUserId().toString(),
                participantInfo.getUsername(),
                participantInfo.getFirstName(),
                participantInfo.getLastName(),
                participantInfo.getAvatar());
    }

}
