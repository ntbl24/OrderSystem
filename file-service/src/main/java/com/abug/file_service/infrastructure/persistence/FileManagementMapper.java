package com.abug.file_service.infrastructure.persistence;

import com.abug.file_service.domain.FileId;
import com.abug.file_service.domain.FileManagement;
import com.abug.file_service.domain.OwnerId;
import com.abug.file_service.infrastructure.persistence.document.FileManagementDocument;

public class FileManagementMapper {
    public static FileManagement domain(FileManagementDocument entity){
        return FileManagement.create(FileId.of(entity.getId()), OwnerId.of(entity.getOwnerId()), entity.getContentType(),
                entity.getSize(), entity.getMd5Checksum(), entity.getPath());
    }

    public static FileManagementDocument document(FileManagement domain){
        return new FileManagementDocument(domain.getId().value(), domain.getOwnerId().value(), domain.getContentType(),
                domain.getSize(), domain.getMd5Checksum(), domain.getPath());
    }
}
