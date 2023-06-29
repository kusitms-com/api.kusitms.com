package com.kusitms.website.domain.introduction.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kusitms.website.domain.introduction.entity.ManageTeam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema
public class ManagementTeamResponse {
    @Schema(description = "운영진 팀 이름", example = "학부학")
    private String name;

    @Schema(description = "운영진 이미지 링크", example = "https://kusitms-bucket.s3.ap-northeast-2.amazonaws.com/intro/management/학부학.jpg")
    private String imageLink;

    @Schema(description = "운영진 팀 소개", example = "학부학은 ~을 관리합니다.")
    private String description;

    public static ManagementTeamResponse fromEntity(ManageTeam manageTeam) {
        return ManagementTeamResponse.builder()
                .description(manageTeam.getDescription())
                .name(manageTeam.getName())
                .imageLink(manageTeam.getImageUrl())
                .build();
    }
}
