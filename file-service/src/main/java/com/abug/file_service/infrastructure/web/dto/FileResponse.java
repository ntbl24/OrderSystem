package com.abug.file_service.infrastructure.web.dto;

import com.abug.file_service.application.dto.FileInfo;
import com.abug.file_service.application.dto.UploadFileResult;
import com.abug.file_service.domain.FileManagement;

public record FileResponse(String originalFileName, String url) {
    public static FileResponse from(UploadFileResult file){
        return new FileResponse(file.originalFileName(), file.url());
    }
}
