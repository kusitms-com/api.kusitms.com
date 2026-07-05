package com.kusitms.website.domain.user.dto.response;

import com.kusitms.website.domain.mentoring.entity.ApplicationStatus;
import com.kusitms.website.domain.mentoring.entity.MentoringApplication;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@Schema(description = "OB 멘토링 요청 카드 응답")
public class OBMentoringRequestCardResponse {

    @Schema(description = "멘토링 신청 ID")
    private Long applicationId;

    @Schema(description = "멘티 프로필 이미지 URL")
    private String menteeProfileImageUrl;

    @Schema(description = "멘티 이름")
    private String menteeName;

    @Schema(description = "멘티 메시지")
    private String message;

    @Schema(description = "신청 날짜")
    private LocalDate date;

    @Schema(description = "신청 시작 시간")
    private LocalTime startTime;

    @Schema(description = "신청 종료 시간")
    private LocalTime endTime;

    @Schema(description = "신청 상태")
    private ApplicationStatus status;

    @Schema(description = "채팅방 ID")
    private Long chatRoomId;

    public static OBMentoringRequestCardResponse from(MentoringApplication application) {
        return OBMentoringRequestCardResponse.builder()
                .applicationId(application.getApplicationId())
                .menteeProfileImageUrl(application.getApplicant().getProfileImageUrl())
                .menteeName(application.getApplicant().getName())
                .message(application.getMessage())
                .date(application.getSlot().getDate())
                .startTime(application.getSlot().getStartTime())
                .endTime(application.getSlot().getEndTime())
                .status(application.getStatus())
                .chatRoomId(null)
                .build();
    }
}
