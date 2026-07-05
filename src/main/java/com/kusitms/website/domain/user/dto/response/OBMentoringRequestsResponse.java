package com.kusitms.website.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "OB 멘토링 요청 목록 응답")
public class OBMentoringRequestsResponse {

    @Schema(description = "전체 요청 수")
    private long totalCount;

    @Schema(description = "전체 페이지 수")
    private int totalPages;

    @Schema(description = "현재 페이지")
    private int currentPage;

    @Schema(description = "대기 중 요청 목록")
    private List<OBMentoringRequestCardResponse> pendingRequests;

    @Schema(description = "진행 중 요청 목록")
    private List<OBMentoringRequestCardResponse> activeRequests;

    @Schema(description = "완료 요청 목록")
    private List<OBMentoringRequestCardResponse> completedRequests;

    @Schema(description = "거절 요청 목록")
    private List<OBMentoringRequestCardResponse> rejectedRequests;
}
