package com.abug.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

public record NotificationEvent(
        String eventId,
        String channel,
        String recipient,
        String templateCode,
        Map<String, Object> param,
        String subject,
        String body
) implements IntegrationEvent {

    @Override
    public String eventType() {
        return "NotificationRequest";
    }
}
