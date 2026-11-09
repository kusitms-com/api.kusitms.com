package com.kusitms.website.domain.introduction.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kusitms.website.domain.introduction.entity.ExpertLecture;
import com.kusitms.website.domain.introduction.entity.Introduction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Schema
public class ExpertLectureRequest {
    @Schema(description = "강연자 이름", example = "김전문")
    private String name;

    @Schema(description = "강연자 프로필 파일")
    private MultipartFile imageFile;

    @Schema(description = "유지할 기존 강연자 프로필 URL (새 파일이 없으면 이 이미지를 유지, 조회 응답의 값 그대로)", example = "https://kusitms-bucket.s3.ap-northeast-2.amazonaws.com/intro/image.png")
    private String imageUrl;

    @Schema(description = "강연자 소속 기업", example = "현 큐시즘")
    private String corporation;

    @Schema(description = "강연 내용", example = "큐시즘 바로 알기")
    private String description;

    public static ExpertLecture from(ExpertLectureRequest request, String imageUrl,
                                     Introduction introduction) {
        return ExpertLecture.builder()
                .name(request.getName())
                .corporation(request.getCorporation())
                .description(request.getDescription())
                .introduction(introduction)
                .imageUrl(imageUrl)
                .build();
    }
}
