package com.abug.event.dto;

public record EventMetadata(
        String aggregateId,
        String aggregateType,
        String topic
) {
}
