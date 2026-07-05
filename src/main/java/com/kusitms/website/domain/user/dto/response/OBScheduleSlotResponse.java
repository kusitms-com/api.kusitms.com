package com.kusitms.website.domain.user.dto.response;

import com.kusitms.website.domain.mentoring.entity.MentoringSlot;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class OBScheduleSlotResponse {

    private Long slotId;
    private LocalTime startTime;
    private LocalTime endTime;
    private int currentApplicants;
    private boolean locked;

    public static OBScheduleSlotResponse from(MentoringSlot slot, int currentApplicants) {
        return OBScheduleSlotResponse.builder()
                .slotId(slot.getSlotId())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .currentApplicants(currentApplicants)
                .locked(currentApplicants > 0)
                .build();
    }
}
