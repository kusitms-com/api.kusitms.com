package com.kusitms.website.domain.user.dto.request;

import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
public class OBScheduleUpdateRequest {

    @NotNull(message = "날짜는 필수입니다.")
    private LocalDate date;

    @NotNull(message = "소그룹 여부는 필수입니다.")
    private Boolean groupMentoring;

    @Min(value = 2, message = "최대 인원은 2명 이상이어야 합니다.")
    @Max(value = 5, message = "최대 인원은 5명 이하여야 합니다.")
    private Integer maxAttendees;

    private List<LocalTime> startTimes;
}
