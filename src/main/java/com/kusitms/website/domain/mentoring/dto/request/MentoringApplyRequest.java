package com.kusitms.website.domain.mentoring.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class MentoringApplyRequest {

    @NotNull
    @Schema(description = "슬롯 ID", required = true)
    private Long slotId;

    @Size(max = 500)
    @Schema(description = "멘토에게 공유할 메시지", maxLength = 500)
    private String message;
}
