package com.abug.file_service.application.dto;

import org.springframework.core.io.Resource;

public record FileData(
        String fileName,
        String contentType,
        Resource resource
) {
}
