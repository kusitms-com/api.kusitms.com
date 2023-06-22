package com.kusitms.website.domain.introduction.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema
public class OBLectureResponse {
    @JsonProperty("name")
    @Schema(description = "강연자 이름", example = "김오비")
    private String name;

    @JsonProperty("image_url")
    @Schema(description = "강연자 프로필 사진", example = "https://kusitms-bucket.s3.ap-northeast-2.amazonaws.com/intro/lecture/김오비_프로필.jpg")
    private String imageUrl;

    @JsonProperty("topic")
    @Schema(description = "강연 주제", example = "큐시즘 바로 알기")
    private String topic;
}
