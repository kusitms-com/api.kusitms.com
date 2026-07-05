package com.kusitms.website.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "거절 사유 조회 응답")
public class ApplicationRejectionReasonResponse {

    @Schema(description = "멘토링 신청 ID")
    private Long applicationId;

    @Schema(description = "거절 사유")
    private String rejectionReason;
}
