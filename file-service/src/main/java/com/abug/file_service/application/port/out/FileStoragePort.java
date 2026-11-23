package com.abug.file_service.application.port.out;

import com.abug.file_service.application.dto.FileInfo;
import com.abug.file_service.domain.FileId;
import com.abug.file_service.domain.FileManagement;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStoragePort {
    FileInfo save(FileId fileId, MultipartFile file);
    Resource read(FileManagement fileManagement);
}
