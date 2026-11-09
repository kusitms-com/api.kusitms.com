package com.kusitms.website.domain.introduction.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kusitms.website.domain.introduction.dto.response.ManagementTeamResponse;
import com.kusitms.website.domain.introduction.entity.Introduction;
import com.kusitms.website.domain.introduction.entity.ManageTeam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Schema
public class ManagementTeamRequest {
    @Schema(description = "운영진 팀 이름", example = "학부학")
    private String name;

    @Schema(description = "운영진 이미지 파일")
    private MultipartFile imageFile;

    @Schema(description = "유지할 기존 운영진 이미지 URL (새 파일이 없으면 이 이미지를 유지, 조회 응답의 값 그대로)", example = "https://kusitms-bucket.s3.ap-northeast-2.amazonaws.com/intro/image.png")
    private String imageUrl;

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
