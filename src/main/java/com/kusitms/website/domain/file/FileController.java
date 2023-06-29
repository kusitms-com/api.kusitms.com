package com.kusitms.website.domain.file;

import com.kusitms.website.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Hidden
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final S3Service s3Service;

    @PostMapping()
    public ResponseEntity<BaseResponse> postFile(@RequestPart("file_request") MultipartFile fileRequest,
                                                 @RequestPart("directory_name") String directoryName) {
        return ResponseEntity.ok(new BaseResponse(s3Service.uploadFile(fileRequest, directoryName)));
    }
}
