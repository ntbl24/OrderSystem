package com.abug.file_service.infrastructure.persistence.adapter;

import com.abug.file_service.application.port.out.FileManagementRepository;
import com.abug.file_service.domain.FileId;
import com.abug.file_service.domain.FileManagement;
import com.abug.file_service.infrastructure.persistence.FileManagementMapper;
import com.abug.file_service.infrastructure.persistence.repository.FileManagementMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaFileManagementAdapter implements FileManagementRepository {
    private final FileManagementMongoRepository repository;

    @Override
    public FileManagement save(FileManagement file) {
        return FileManagementMapper.domain(repository.save(FileManagementMapper.document(file)));
    }

    @Override
    public Optional<FileManagement> findById(FileId fileId) {
        return repository.findById(fileId.value())
                .map(FileManagementMapper::domain);
    }
}
