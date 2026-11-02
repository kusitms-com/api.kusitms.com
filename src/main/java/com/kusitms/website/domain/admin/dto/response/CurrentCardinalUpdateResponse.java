package com.kusitms.website.domain.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "현재 기수 변경 응답")
public class CurrentCardinalUpdateResponse {
    @Schema(description = "변경 전 기수", example = "33")
    private Integer previousCardinal;

    @Schema(description = "변경 후 기수", example = "34")
    private Integer currentCardinal;

    @Schema(description = "YB → OB로 변경된 회원 수", example = "50")
    private int changedToOb;

    @Schema(description = "OB → YB로 변경된 회원 수 (기수를 되돌린 경우)", example = "0")
    private int changedToYb;
}
