package com.kusitms.website.domain.project.dto.request;

import com.kusitms.website.domain.admin.entity.TMPCorporateProject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Schema
public class CorporateRequest {
    @Schema(description = "기프 ID (PUT API에서 사용)", example = "1")
    private Long corporateId;

    @Schema(description = "프로젝트 진행 기수", example = "26")
    private int cardinal;

    @Schema(description = "기업 이름", example = "큐시즘")
    private String name;

    @Schema(description = "주제 한 줄 소개", example = "큐시즘 관리자 페이지 만들기")
    private String content;

    @Schema(description = "태그")
    private List<String> tag;

    @Schema(description = "로고 이미지 파일")
    private MultipartFile logoFile;

    @Schema(description = "배너 이미지 파일")
    private MultipartFile bannerFile;

    public static TMPCorporateProject from(CorporateRequest request, String category,
                                           String logoUrl, String bannerUrl) {
        return TMPCorporateProject.builder()
                .cardinal(request.getCardinal())
                .name(request.getName())
                .content(request.getContent())
                .logoUrl(logoUrl)
                .bannerUrl(bannerUrl)
                .category(category)
                .build();
    }
}
