package com.kusitms.website.domain.introduction.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema
public class CountIntroResponse {
    @JsonProperty("member_count")
    @Schema(description = "누적 학회원 수", example = "1432")
    private Long memberCount;

    @JsonProperty("project_count")
    @Schema(description = "누적 프로젝트 수", example = "322")
    private Long projectCount;

    @JsonProperty("university_count")
    @Schema(description = "누적 대학 수", example = "78")
    private Long universityCount;
}
