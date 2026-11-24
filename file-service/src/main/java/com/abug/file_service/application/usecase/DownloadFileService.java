package com.abug.file_service.application.usecase;

import com.abug.file_service.application.dto.FileData;
import com.abug.file_service.application.port.in.DownloadFileUseCase;
import com.abug.file_service.application.port.out.FileManagementRepository;
import com.abug.file_service.application.port.out.FileStoragePort;
import com.abug.file_service.common.exception.AppException;
import com.abug.file_service.common.exception.ErrorCode;
import com.abug.file_service.domain.FileId;
import com.abug.file_service.domain.FileManagement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DownloadFileService implements DownloadFileUseCase {

    private final FileManagementRepository fileManagementRepository;
    private final FileStoragePort fileStoragePort;

    @Override
    public FileData download(String fileName) {
        FileManagement fileMgmt = fileManagementRepository.findById(FileId.of(fileName))
                .orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_FOUND));
        Resource resource = fileStoragePort.read(fileMgmt);

        return new FileData(fileMgmt.getId().toString(), fileMgmt.getContentType(), resource);
    }
}
