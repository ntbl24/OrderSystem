package com.abug.file_service.infrastructure.persistence.document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "file_management")
public class FileManagementDocument {
    @MongoId
    UUID id;
    UUID ownerId;
    String contentType;
    long size;
    String md5Checksum;
    String path;
}
