package com.abug.profile.application.port.out;

import org.springframework.web.multipart.MultipartFile;

public interface MediaUploadPort {

    Snapshot uploadMedia(MultipartFile file);

    record Snapshot(String url){}
}
