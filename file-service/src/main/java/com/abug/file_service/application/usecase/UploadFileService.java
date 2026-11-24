package com.abug.file_service.application.usecase;

import com.abug.file_service.application.dto.FileInfo;
import com.abug.file_service.application.dto.UploadFileResult;
import com.abug.file_service.application.port.in.UploadFileUseCase;
import com.abug.file_service.application.port.out.FileManagementRepository;
import com.abug.file_service.application.port.out.FileStoragePort;
import com.abug.file_service.domain.FileManagement;
import com.abug.file_service.domain.FileId;
import com.abug.file_service.domain.OwnerId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadFileService implements UploadFileUseCase {

    private final FileManagementRepository fileManagementRepository;
    private final FileStoragePort fileStoragePort;

    @Override
    public UploadFileResult uploadFile(MultipartFile file) {

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        FileId fileId = FileId.generate();

        FileInfo fileInfo = fileStoragePort.save(fileId, file);

        FileManagement fileMgmt = FileManagement.create(fileId, OwnerId.of(userId),
                file.getContentType(), file.getSize(), fileInfo.md5Checksum(), fileInfo.path());

        fileManagementRepository.save(fileMgmt);

        return new UploadFileResult(file.getOriginalFilename(), fileInfo.url());
    }
}
