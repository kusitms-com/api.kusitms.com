package com.kusitms.website.domain.user.dto.response;

import com.kusitms.website.domain.mentoring.entity.MentoringSlot;
import com.kusitms.website.domain.mentoring.entity.SlotType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
public class OBScheduleDateResponse {

    private LocalDate date;
    private boolean groupMentoring;
    private int maxAttendees;
    private List<OBScheduleSlotResponse> slots;

    public static OBScheduleDateResponse from(LocalDate date, List<MentoringSlot> slots, Map<Long, Integer> applicantCountMap) {
        SlotType slotType = slots.get(0).getSlotType();
        int maxAttendees = slotType == SlotType.ONE_TO_N ? slots.get(0).getMaxAttendees() : 1;

        return OBScheduleDateResponse.builder()
                .date(date)
                .groupMentoring(slotType == SlotType.ONE_TO_N)
                .maxAttendees(maxAttendees)
                .slots(slots.stream()
                        .map(slot -> OBScheduleSlotResponse.from(
                                slot, applicantCountMap.getOrDefault(slot.getSlotId(), 0)))
                        .collect(Collectors.toList()))
                .build();
    }
}
