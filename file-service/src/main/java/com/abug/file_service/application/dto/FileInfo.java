package com.abug.file_service.application.dto;

public record FileInfo(
        String name, String contentType, long size, String md5Checksum, String path, String url
) {
}
