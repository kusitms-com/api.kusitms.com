package com.kusitms.website.domain.introduction.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kusitms.website.domain.introduction.entity.Activity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema
public class ActivityResponse {
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
                .name(activity.getName())
                .imageUrl1(activity.getImageUrl1())
                .imageUrl2(activity.getImageUrl2())
                .description(activity.getDescription())
                .build();
    }
}