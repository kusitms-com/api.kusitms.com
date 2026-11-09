package com.kusitms.website.domain.introduction.dto.request;

import com.kusitms.website.domain.introduction.entity.Activity;
import com.kusitms.website.domain.introduction.entity.Introduction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Schema
public class ActivityRequest implements IntroListItemRequest {
    @Schema(description = "기존 항목 id (어드민 조회 응답의 id). 새 항목이면 생략", example = "1")
    private Long id;

    @Schema(description = "활동명", example = "밋업데이")
    private String name;

    @Schema(description = "활동 이미지 1 (기존 항목은 생략하면 이미지 유지)")
    private MultipartFile imageFile1;

    @Schema(description = "활동 이미지 2 (기존 항목은 생략하면 이미지 유지)")
    private MultipartFile imageFile2;

    @Schema(description = "기존 활동 이미지 1 삭제 여부", example = "false")
    private Boolean removeImage1;

    @Schema(description = "기존 활동 이미지 2 삭제 여부", example = "false")
    private Boolean removeImage2;

    @Schema(description = "활동 설명글", example = "밋업데이는 ~입니다.")
    private String description;

    public static Activity from(ActivityRequest request, String imageUrl1, String imageUrl2,
                                Introduction introduction, int displayOrder) {
        return Activity.builder()
                .name(request.getName())
                .imageUrl1(imageUrl1)
                .imageUrl2(imageUrl2)
                .description(request.getDescription())
                .introduction(introduction)
                .displayOrder(displayOrder)
                .build();
    }
}
