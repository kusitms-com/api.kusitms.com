package com.kusitms.website.domain.mentoring.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KeywordChipResponse {

    @Schema(description = "키워드 ID")
    private Long keywordId;

    @Schema(description = "키워드 이름")
    private String name;

    @Schema(description = "선택 횟수")
    private Long count;
}
