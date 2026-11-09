package com.kusitms.website.domain.introduction.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kusitms.website.global.util.S3Util;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "어드민 이미지 목록 항목 (수정 시 id를 보내면 이미지 유지)")
public class IntroImageResponse {
    @Schema(description = "항목 id", example = "1")
    private Long id;

    @JsonProperty("image_url")
    @Schema(description = "이미지 URL", example = "https://kusitms-bucket.s3.ap-northeast-2.amazonaws.com/intro/logo.png")
    private String imageUrl;

    public static IntroImageResponse of(Long id, String key) {
        return new IntroImageResponse(id, S3Util.toUrl(key));
    }
}
