package com.kusitms.website.domain.mentoring.dto.response;

import com.kusitms.website.domain.mentoring.entity.MentoringReview;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MentoringReviewDetailResponse {

    @Schema(description = "후기 ID")
    private Long reviewId;

    @Schema(description = "작성자 이름")
    private String reviewerName;

    @Schema(description = "작성자 기수")
    private Integer reviewerCardinal;

    @Schema(description = "후기 내용")
    private String content;

    @Schema(description = "선택된 키워드 목록")
    private List<String> keywords;

    @Schema(description = "작성일")
    private LocalDateTime createdAt;

    public static MentoringReviewDetailResponse from(MentoringReview review) {
        List<String> keywordNames = review.getKeywords().stream()
                .map(rk -> rk.getKeyword().getName())
                .collect(Collectors.toList());

        return MentoringReviewDetailResponse.builder()
                .reviewId(review.getReviewId())
                .reviewerName(review.getReviewer().getName())
                .reviewerCardinal(review.getReviewer().getCardinal())
                .content(review.getContent())
                .keywords(keywordNames)
                .createdAt(review.getCreatedAt())
                .build();
    }
}
