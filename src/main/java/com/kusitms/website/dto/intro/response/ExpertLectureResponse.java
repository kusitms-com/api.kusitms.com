package com.kusitms.website.dto.intro.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema
public class ExpertLectureResponse {
    @JsonProperty("name")
    @Schema(description = "강연자 이름", example = "김전문")
    private String name;

    @JsonProperty("image_url")
    @Schema(description = "강연자 프로필 사진", example = "https://kusitms-bucket.s3.ap-northeast-2.amazonaws.com/intro/lecture/김전문_프로필.jpg")
    private String imageUrl;

    @JsonProperty("corporation")
    @Schema(description = "강연자 소속 기업", example = "현 큐시즘")
    private String corporation;

    @JsonProperty("description")
    @Schema(description = "강연 내용", example = "큐시즘 바로 알기")
    private String description;
}
