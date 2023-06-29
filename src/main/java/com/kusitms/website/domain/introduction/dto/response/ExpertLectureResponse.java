package com.kusitms.website.domain.introduction.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kusitms.website.domain.introduction.entity.ExpertLecture;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema
public class ExpertLectureResponse {

    @Schema(description = "강연자 이름", example = "김전문")
    private String name;

    @JsonProperty("image_link")
    @Schema(description = "강연자 프로필 사진", example = "https://kusitms-bucket.s3.ap-northeast-2.amazonaws.com/intro/lecture/김전문_프로필.jpg")
    private String imageLink;

    @Schema(description = "강연자 소속 기업", example = "현 큐시즘")
    private String corporation;

    @Schema(description = "강연 내용", example = "큐시즘 바로 알기")
    private String description;

    public static ExpertLectureResponse fromEntity(ExpertLecture expertLecture) {
        return ExpertLectureResponse.builder()
                .name(expertLecture.getName())
                .imageLink(expertLecture.getImageUrl())
                .corporation(expertLecture.getCorporation())
                .description(expertLecture.getDescription())
                .build();
    }
}
