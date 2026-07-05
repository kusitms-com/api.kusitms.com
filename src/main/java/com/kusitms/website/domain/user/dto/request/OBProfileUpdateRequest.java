package com.kusitms.website.domain.user.dto.request;

import com.kusitms.website.domain.mentoring.entity.MentoringCategory;
import com.kusitms.website.domain.mentoring.entity.MentoringMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@Schema(description = "OB 프로필 수정 요청")
public class OBProfileUpdateRequest {

    @NotBlank
    @Schema(description = "경력", required = true)
    private String experience;

    @NotBlank
    @Schema(description = "멘토링 제목", required = true)
    private String title;

    @NotBlank
    @Schema(description = "멘토링 소개", required = true)
    private String introduction;

    @NotNull
    @Schema(description = "직무", required = true)
    private MentoringCategory category;

    @NotNull
    @Schema(description = "멘토링 방식", required = true)
    private MentoringMethod method;

    @NotNull
    @Min(30)
    @Schema(description = "멘토링 한타임 시간(분)", required = true)
    private Integer durationMinutes;

    @NotNull
    @Min(0)
    @Schema(description = "멘토링 금액", required = true)
    private Integer pricePerHour;
}
