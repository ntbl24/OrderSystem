package com.abug.file_service.application.port.in;

import com.abug.file_service.application.dto.FileData;
import org.springframework.core.io.Resource;

public interface DownloadFileUseCase {
    FileData download(String fileName);
}
