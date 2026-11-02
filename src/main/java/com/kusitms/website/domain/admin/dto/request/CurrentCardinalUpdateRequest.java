package com.kusitms.website.domain.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "현재 기수 변경 요청")
public class CurrentCardinalUpdateRequest {
    @Schema(description = "변경할 현재 기수", example = "34")
    private Integer cardinal;

    public CurrentCardinalUpdateRequest(Integer cardinal) {
        this.cardinal = cardinal;
    }
}
