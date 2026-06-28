package com.kusitms.website.domain.mentoring.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MentoringReviewListResponse {

    @Schema(description = "전체 후기 수")
    private long totalCount;

    @Schema(description = "전체 페이지 수")
    private int totalPages;

    @Schema(description = "현재 페이지")
    private int currentPage;

    @Schema(description = "후기 목록")
    private List<MentoringReviewDetailResponse> reviews;
}
