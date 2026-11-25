package com.abug.profile.infrastructure.external.httpclient;

import com.abug.profile.application.port.out.MediaUploadPort;
import com.abug.profile.infrastructure.external.httpclient.dto.FileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class MediaUploadAdapter implements MediaUploadPort {
    private final FileClient fileClient;
    @Override
    public Snapshot uploadMedia(MultipartFile file) {
        var response = fileClient.uploadMedia(file);
        return new Snapshot(response.getData().url());
    }
}
