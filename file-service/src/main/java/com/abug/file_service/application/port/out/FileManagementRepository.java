package com.abug.file_service.application.port.out;

import com.abug.file_service.domain.FileId;
import com.abug.file_service.domain.FileManagement;

import java.util.Optional;

public interface FileManagementRepository {
    FileManagement save(FileManagement file);
    Optional<FileManagement> findById(FileId fileId);
}
