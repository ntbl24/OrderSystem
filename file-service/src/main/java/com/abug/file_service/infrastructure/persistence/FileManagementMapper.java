package com.abug.file_service.infrastructure.persistence;

import com.abug.file_service.domain.FileId;
import com.abug.file_service.domain.FileManagement;
import com.abug.file_service.domain.OwnerId;
import com.abug.file_service.infrastructure.persistence.entity.FileManagementEntity;

public class FileManagementMapper {
    public static FileManagement domain(FileManagementEntity entity){
        return FileManagement.create(FileId.of(entity.getId()), OwnerId.of(entity.getOwnerId()), entity.getContentType(),
                entity.getSize(), entity.getMd5Checksum(), entity.getPath());
    }

    public static FileManagementEntity entity(FileManagement domain){
        return new FileManagementEntity(domain.getId().value(), domain.getOwnerId().value(), domain.getContentType(),
                domain.getSize(), domain.getMd5Checksum(), domain.getPath());
    }
}
