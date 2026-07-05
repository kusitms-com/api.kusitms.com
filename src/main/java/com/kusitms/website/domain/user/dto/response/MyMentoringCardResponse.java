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
@Schema(description = "마이페이지 멘토링 카드 응답")
public class MyMentoringCardResponse {

    @Schema(description = "멘토링 신청 ID")
    private Long applicationId;

    @Schema(description = "멘토 프로필 이미지 URL")
    private String mentorProfileImageUrl;

    @Schema(description = "멘토 이름")
    private String mentorName;

    @Schema(description = "멘토링 제목")
    private String mentoringTitle;

    @Schema(description = "멘토링 날짜")
    private LocalDate date;

    @Schema(description = "멘토링 시작 시간")
    private LocalTime startTime;

    @Schema(description = "멘토링 종료 시간")
    private LocalTime endTime;

    @Schema(description = "신청 상태")
    private ApplicationStatus status;

    @Schema(description = "채팅방 ID")
    private Long chatRoomId;

    @Schema(description = "후기 작성 가능 여부")
    private boolean reviewWritable;

    @Schema(description = "거절 사유 조회 가능 여부")
    private boolean rejectionReasonVisible;

    public static MyMentoringCardResponse from(MentoringApplication application, boolean reviewWritable) {
        return MyMentoringCardResponse.builder()
                .applicationId(application.getApplicationId())
                .mentorProfileImageUrl(application.getSlot().getMentor().getProfileImageUrl())
                .mentorName(application.getSlot().getMentor().getMember().getName())
                .mentoringTitle(application.getSlot().getMentor().getTitle())
                .date(application.getSlot().getDate())
                .startTime(application.getSlot().getStartTime())
                .endTime(application.getSlot().getEndTime())
                .status(application.getStatus())
                .chatRoomId(null)
                .reviewWritable(reviewWritable)
                .rejectionReasonVisible(application.getStatus() == ApplicationStatus.REJECTED)
                .build();
    }
}
