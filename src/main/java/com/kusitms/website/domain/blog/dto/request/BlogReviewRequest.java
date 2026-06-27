package com.kusitms.website.domain.blog.dto.request;

import com.kusitms.website.domain.admin.entity.BlogReviewActivity;
import com.kusitms.website.domain.admin.entity.TMPBlogReview;
import com.kusitms.website.domain.project.entity.Team;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Schema
public class BlogReviewRequest {
    @Schema(description = "블로그 후기 ID (PUT API에서 사용)", example = "1")
    private Long blogReviewId;

    @Schema(description = "기수", example = "33")
    private Integer cardinal;

    @Schema(description = "파트", example = "PLANNER, DESIGNER, DEVELOPER")
    private Team part;

    @Schema(description = "활동", example = "DOCUMENT_REVIEW, INTERVIEW_REVIEW, COMPANY_PROJECT")
    private BlogReviewActivity activity;

    @Schema(description = "썸네일 이미지 파일")
    private MultipartFile thumbnailFile;

    @Schema(description = "제목", example = "면접에서 이런 질문이 나왔어요")
    private String title;

    @Schema(description = "미리보기 텍스트", example = "합격에 도움이 됐던 준비 과정을 공유합니다.")
    private String previewText;

    public static TMPBlogReview from(BlogReviewRequest request, String thumbnailUrl) {
        return TMPBlogReview.builder()
                .cardinal(request.getCardinal())
                .part(request.getPart())
                .activity(request.getActivity())
                .thumbnailUrl(thumbnailUrl)
                .title(request.getTitle())
                .previewText(request.getPreviewText())
                .build();
    }
}
