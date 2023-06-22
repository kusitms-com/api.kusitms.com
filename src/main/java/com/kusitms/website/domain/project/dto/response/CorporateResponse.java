package com.kusitms.website.domain.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema
public class CorporateResponse {
    @Schema(description = "기프 개수")
    private int corporateCount;
    @Schema(description = "기프 리스트")
    private List<CorporateDetailResponse> corporateList;
}
