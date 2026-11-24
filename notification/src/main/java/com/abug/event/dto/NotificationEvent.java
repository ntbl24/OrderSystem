package com.abug.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEvent {
    private String eventId;

    private String channel;
    private String recipient;
    private String templateCode;
    private Map<String, Object> param;
    private String subject;
    private String body;
}
