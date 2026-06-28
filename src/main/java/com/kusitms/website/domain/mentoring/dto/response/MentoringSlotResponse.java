package com.kusitms.website.domain.mentoring.dto.response;

import com.kusitms.website.domain.mentoring.entity.MentoringSlot;
import com.kusitms.website.domain.mentoring.entity.SlotType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class MentoringSlotResponse {

    @Schema(description = "슬롯 ID")
    private Long slotId;

    @Schema(description = "날짜")
    private LocalDate date;

    @Schema(description = "시작 시간")
    private LocalTime startTime;

    @Schema(description = "종료 시간")
    private LocalTime endTime;

    @Schema(description = "슬롯 타입")
    private SlotType slotType;

    @Schema(description = "최대 인원")
    private int maxAttendees;

    @Schema(description = "현재 신청 인원 (PENDING + ACTIVE)")
    private int currentAttendees;

    @Schema(description = "신청 가능 여부")
    private boolean available;

    public static MentoringSlotResponse from(MentoringSlot slot, int currentAttendees) {
        boolean available;
        if (slot.getSlotType() == SlotType.ONE_TO_ONE) {
            available = currentAttendees == 0;
        } else {
            available = currentAttendees < slot.getMaxAttendees();
        }

        return MentoringSlotResponse.builder()
                .slotId(slot.getSlotId())
                .date(slot.getDate())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .slotType(slot.getSlotType())
                .maxAttendees(slot.getMaxAttendees())
                .currentAttendees(currentAttendees)
                .available(available)
                .build();
    }
}
