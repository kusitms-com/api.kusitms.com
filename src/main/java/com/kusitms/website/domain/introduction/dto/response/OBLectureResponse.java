package com.kusitms.website.domain.introduction.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kusitms.website.domain.introduction.entity.OBLecture;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema
public class OBLectureResponse {

    @Schema(description = "강연자 이름", example = "김오비")
    private String name;

    @JsonProperty("image_link")
    @Schema(description = "강연자 프로필 사진", example = "https://kusitms-bucket.s3.ap-northeast-2.amazonaws.com/intro/lecture/김오비_프로필.jpg")
    private String imageLink;

    @Schema(description = "강연 주제", example = "큐시즘 바로 알기")
    private String topic;

    public static OBLectureResponse fromEntity(OBLecture obLecture) {
        return OBLectureResponse.builder()
                .name(obLecture.getName())
                .imageLink(obLecture.getImageUrl())
                .topic(obLecture.getTopic())
                .build();
    }
}
