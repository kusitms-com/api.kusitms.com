package com.kusitms.website.domain.review.dto.request;

import com.kusitms.website.domain.admin.entity.TMPReview;
import com.kusitms.website.domain.project.entity.Team;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema
public class ReviewRequest {
    @Schema(description = "리뷰 아이디", example = "1")
    private Long reviewId;

    @Schema(description = "이름", example = "김큐시")
    private String name;

    @Schema(description = "기수", example = "33")
    private Integer cardinal;

    @Schema(description = "소속팀", example = "PLANNER, DESIGNER, DEVELOPER")
    private Team team;

    @Schema(description = "리뷰 내용", example = "리뷰 내용 입니다.")
    private String review;

    public static TMPReview from(ReviewRequest request) {
        return TMPReview.builder()
                .name(request.getName())
                .team(request.getTeam())
                .review(request.getReview())
                .build();
    }
}
