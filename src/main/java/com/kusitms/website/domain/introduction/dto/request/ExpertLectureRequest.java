package com.kusitms.website.domain.introduction.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kusitms.website.domain.introduction.entity.ExpertLecture;
import com.kusitms.website.domain.introduction.entity.Introduction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Schema
public class ExpertLectureRequest {
    @JsonProperty("name")
    @Schema(description = "강연자 이름", example = "김전문")
    private String name;

    @JsonProperty("image_file")
    @Schema(description = "강연자 프로필 파일")
    private MultipartFile imageFile;

    @JsonProperty("corporation")
    @Schema(description = "강연자 소속 기업", example = "현 큐시즘")
    private String corporation;

    @JsonProperty("description")
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
