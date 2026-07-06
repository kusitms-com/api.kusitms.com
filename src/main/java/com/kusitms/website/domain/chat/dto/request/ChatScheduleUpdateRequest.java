package com.kusitms.website.domain.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Schema(description = "채팅 일정 수정 요청")
public class ChatScheduleUpdateRequest {

    @NotNull(message = "변경 날짜는 필수입니다.")
    @Schema(description = "변경 날짜", required = true)
    private LocalDate scheduledDate;

    @NotNull(message = "변경 시작 시간은 필수입니다.")
    @Schema(description = "변경 시작 시간", required = true)
    private LocalTime scheduledStartTime;

    @NotNull(message = "변경 종료 시간은 필수입니다.")
    @Schema(description = "변경 종료 시간", required = true)
    private LocalTime scheduledEndTime;
}
