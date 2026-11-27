package com.abug.event.dto;

import java.util.Map;

public record NotificationEvent(
        String eventId,
        String channel,
        String recipient,
        String templateCode,
        Map<String, Object> param,
        String subject,
        String body
){
}
