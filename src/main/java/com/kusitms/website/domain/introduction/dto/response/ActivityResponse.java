package com.kusitms.website.domain.introduction.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kusitms.website.domain.introduction.entity.Activity;
import com.kusitms.website.global.util.S3Util;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema
public class ActivityResponse {
    @Schema(description = "항목 id (어드민 수정 시 사용)", example = "1")
    private Long id;

    @Schema(description = "활동명", example = "밋업데이")
    private String name;

    @JsonProperty("image_url1")
    @Schema(description = "활동 이미지 1 URL")
    private String imageUrl1;

    @JsonProperty("image_url2")
    @Schema(description = "활동 이미지 2 URL")
    private String imageUrl2;

    @Schema(description = "활동 설명글")
    private String description;

    public static ActivityResponse fromEntity(Activity activity) {
        return ActivityResponse.builder()
                .id(activity.getActivityId())
                .name(activity.getName())
                .imageUrl1(S3Util.toUrl(activity.getImageUrl1()))
                .imageUrl2(S3Util.toUrl(activity.getImageUrl2()))
                .description(activity.getDescription())
                .build();
    }
}