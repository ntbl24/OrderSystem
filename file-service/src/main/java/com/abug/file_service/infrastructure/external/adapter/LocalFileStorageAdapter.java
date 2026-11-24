package com.abug.file_service.infrastructure.external.adapter;

import com.abug.file_service.application.dto.FileInfo;
import com.abug.file_service.application.port.out.FileStoragePort;
import com.abug.file_service.common.exception.AppException;
import com.abug.file_service.common.exception.ErrorCode;
import com.abug.file_service.domain.FileId;
import com.abug.file_service.domain.FileManagement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;import java.util.UUID;

@Component
public class LocalFileStorageAdapter implements FileStoragePort {

    @Value("${app.file.storage-dir}")
    private String storageDir;

    @Value("${app.file.download-prefix}")
    private String urlPrefix;

    @Override
    public FileInfo save(FileId fileId, MultipartFile file) {
        try {
            Path folder = Paths.get(storageDir);
            String extension = StringUtils
                    .getFilenameExtension(file.getOriginalFilename());
            String fileName = fileId.value()
                    + (extension != null ? "." + extension : "");

            Path filePath = folder.resolve(fileName).normalize().toAbsolutePath();

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return new FileInfo(fileName, file.getContentType(), file.getSize(),
                    DigestUtils.md5DigestAsHex(file.getInputStream()), filePath.toString(), urlPrefix + "/" + fileId);
        } catch (IOException e){
            throw new AppException(ErrorCode.CANNOT_UPLOAD_FILE);
        }
    }

    @Override
    public Resource read(FileManagement fileManagement) {
        try{
            var data = Files.readAllBytes(Path.of(fileManagement.getPath()));
            return new ByteArrayResource(data);
        } catch (IOException e){
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }
}
