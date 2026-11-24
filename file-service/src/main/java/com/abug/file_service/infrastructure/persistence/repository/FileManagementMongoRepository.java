package com.abug.file_service.infrastructure.persistence.repository;

import com.abug.file_service.infrastructure.persistence.document.FileManagementDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface FileManagementMongoRepository extends MongoRepository<FileManagementDocument, UUID> {
}
