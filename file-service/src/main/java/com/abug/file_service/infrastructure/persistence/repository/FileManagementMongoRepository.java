package com.abug.file_service.infrastructure.persistence.repository;

import com.abug.file_service.infrastructure.persistence.entity.FileManagementEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface FileManagementMongoRepository extends MongoRepository<FileManagementEntity, UUID> {
}
