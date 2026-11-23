package com.abug.file_service.infrastructure.web.controller;

import com.abug.file_service.application.dto.FileData;
import com.abug.file_service.application.port.in.DownloadFileUseCase;
import com.abug.file_service.application.port.in.UploadFileUseCase;
import com.abug.file_service.common.response.ApiResponse;
import com.abug.file_service.infrastructure.web.dto.FileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final UploadFileUseCase uploadFileUseCase;
    private final DownloadFileUseCase downloadFileUseCase;

    @PostMapping("/media/upload")
    ApiResponse<FileResponse> uploadMedia(@RequestParam("file")MultipartFile file) {
        return ApiResponse.success(FileResponse.from(uploadFileUseCase.uploadFile(file)));
    }

    @GetMapping("/media/download/{fileName}")
    ResponseEntity<Resource> downloadMedia(@PathVariable String fileName) {
        FileData fileData = downloadFileUseCase.download(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileData.contentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(fileData.fileName())
                                .build()
                                .toString()
                )
                .body(fileData.resource());
    }
}
