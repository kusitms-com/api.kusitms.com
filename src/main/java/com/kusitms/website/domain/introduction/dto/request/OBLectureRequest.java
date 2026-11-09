package com.kusitms.website.domain.introduction.dto.request;

import com.kusitms.website.domain.introduction.entity.Introduction;
import com.kusitms.website.domain.introduction.entity.OBLecture;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Schema
public class OBLectureRequest implements IntroListItemRequest {
    @Schema(description = "기존 항목 id (어드민 조회 응답의 id). 새 항목이면 생략", example = "1")
    private Long id;

    @Schema(description = "강연자 이름", example = "김오비")
    private String name;

    @Schema(description = "강연자 프로필 사진 파일 (기존 항목은 생략하면 이미지 유지)")
    private MultipartFile imageFile;

    @Schema(description = "기존 이미지 삭제 여부 (새 파일 없이 이미지를 지울 때 true)", example = "false")
    private Boolean removeImage;

    @Schema(description = "강연 주제", example = "큐시즘 바로 알기")
    private String topic;

    public static OBLecture from(OBLectureRequest request, String imageUrl,
                                 Introduction introduction, int displayOrder) {
        return OBLecture.builder()
                .name(request.getName())
                .topic(request.getTopic())
                .imageUrl(imageUrl)
                .introduction(introduction)
                .displayOrder(displayOrder)
                .build();
    }
}
