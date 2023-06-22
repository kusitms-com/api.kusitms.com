package com.kusitms.website.domain.file;

import com.kusitms.website.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "File", description = "파일 업로드 API Document")
public class FileController {
    private final S3Service s3Service;

    @PostMapping()
    @Operation(summary = "파일 업로드", description = "Multipartfile을 업로드 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업로드 성공")
    })
    public ResponseEntity<BaseResponse> postFile(@RequestPart("file_request") MultipartFile fileRequest) throws IOException {
        return ResponseEntity.ok(new BaseResponse(s3Service.uploadFile(fileRequest, "upload")));
    }
}
