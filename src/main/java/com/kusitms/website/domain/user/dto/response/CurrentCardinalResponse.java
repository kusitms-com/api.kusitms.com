package com.kusitms.website.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "현재 기수 응답")
public class CurrentCardinalResponse {
    @Schema(description = "현재 활동 기수")
    private Integer currentCardinal;
}
