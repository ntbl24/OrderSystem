package com.abug.profile.infrastructure.external.httpclient;

import com.abug.profile.common.configuration.AuthenticationRequestInterceptor;
import com.abug.profile.common.response.ApiResponse;
import com.abug.profile.infrastructure.external.httpclient.dto.FileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "file-service", url = "${app.services.file.url}",
    configuration = { AuthenticationRequestInterceptor.class })
public interface FileClient {
    @PostMapping(value = "/media/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<FileResponse> uploadMedia(@RequestPart("file") MultipartFile file);

}
