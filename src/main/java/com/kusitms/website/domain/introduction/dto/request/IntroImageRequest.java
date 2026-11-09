package com.kusitms.website.domain.introduction.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Schema(description = "이미지 목록 항목 (파트너사·후원사 로고, 밋업 사진). id가 있으면 기존 이미지를 유지하고 새 파일이 있으면 교체, id가 없으면 새 파일로 추가한다")
public class IntroImageRequest implements IntroListItemRequest {
    @Schema(description = "기존 항목 id (어드민 조회 응답의 id). 새 항목이면 생략", example = "1")
    private Long id;

    @Schema(description = "새 이미지 파일 (기존 항목은 생략하면 이미지 유지)")
    private MultipartFile imageFile;
}
