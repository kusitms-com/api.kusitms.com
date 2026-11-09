package com.kusitms.website.domain.introduction.dto.request;

import com.kusitms.website.domain.introduction.entity.Introduction;
import com.kusitms.website.domain.introduction.entity.ManageTeam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Schema
public class ManagementTeamRequest implements IntroListItemRequest {
    @Schema(description = "기존 항목 id (어드민 조회 응답의 id). 새 항목이면 생략", example = "1")
    private Long id;

    @Schema(description = "운영진 팀 이름", example = "학부학")
    private String name;

    @Schema(description = "운영진 이미지 파일 (기존 항목은 생략하면 이미지 유지)")
    private MultipartFile imageFile;

    @Schema(description = "기존 이미지 삭제 여부 (새 파일 없이 이미지를 지울 때 true)", example = "false")
    private Boolean removeImage;

    @Schema(description = "운영진 팀 소개", example = "학부학은 ~을 관리합니다.")
    private String description;

    public static ManageTeam from(ManagementTeamRequest request, String imageUrl,
                                  Introduction introduction, int displayOrder) {
        return ManageTeam.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(imageUrl)
                .introduction(introduction)
                .displayOrder(displayOrder)
                .build();
    }
}
