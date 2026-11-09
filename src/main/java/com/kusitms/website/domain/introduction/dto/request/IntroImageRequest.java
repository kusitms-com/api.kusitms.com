package com.kusitms.website.domain.introduction.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Schema(description = "이미지 목록 항목 (파트너사·후원사 로고, 밋업 사진). 새 파일이 있으면 업로드하고, 없으면 기존 이미지 URL을 유지한다")
public class IntroImageRequest {
    @Schema(description = "새 이미지 파일")
    private MultipartFile imageFile;

    @Schema(description = "유지할 기존 이미지 URL (조회 응답의 값 그대로)", example = "https://kusitms-bucket.s3.ap-northeast-2.amazonaws.com/intro/logo.png")
    private String imageUrl;
}
