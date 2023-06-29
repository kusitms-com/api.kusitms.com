package com.kusitms.website.domain.introduction.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kusitms.website.domain.introduction.dto.response.ManagementTeamResponse;
import com.kusitms.website.domain.introduction.entity.Introduction;
import com.kusitms.website.domain.introduction.entity.ManageTeam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Schema
public class ManagementTeamRequest {
    @JsonProperty("name")
    @Schema(description = "운영진 팀 이름", example = "학부학")
    private String name;

    @JsonProperty("image_file")
    @Schema(description = "운영진 이미지 파일")
    private MultipartFile imageFile;

    @JsonProperty("description")
    @Schema(description = "운영진 팀 소개", example = "학부학은 ~을 관리합니다.")
    private String description;

    public static ManageTeam from(ManagementTeamRequest request, String imageUrl,
                                  Introduction introduction) {
        return ManageTeam.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(imageUrl)
                .introduction(introduction)
                .build();
    }
}
