package com.abug.file_service.application.port.in;

import com.abug.file_service.application.dto.FileInfo;
import com.abug.file_service.application.dto.UploadFileResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadFileUseCase {
    UploadFileResult uploadFile(MultipartFile file);
}
