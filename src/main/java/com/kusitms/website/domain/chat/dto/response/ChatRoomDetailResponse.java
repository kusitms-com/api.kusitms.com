package com.kusitms.website.domain.chat.dto.response;

import com.kusitms.website.domain.chat.entity.ChatRoom;
import com.kusitms.website.domain.chat.entity.ChatRoomStatus;
import com.kusitms.website.domain.mentoring.entity.MentoringMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@Schema(description = "채팅방 상세 조회 응답")
public class ChatRoomDetailResponse {

    @Schema(description = "채팅방 ID")
    private Long roomId;

    @Schema(description = "상대방 프로필 이미지 URL")
    private String partnerProfileImageUrl;

    @Schema(description = "상대방 이름")
    private String partnerName;

    @Schema(description = "멘토링 방식")
    private MentoringMethod method;

    @Schema(description = "멘토링 날짜")
    private LocalDate scheduledDate;

    @Schema(description = "멘토링 시작 시간")
    private LocalTime scheduledStartTime;

    @Schema(description = "멘토링 종료 시간")
    private LocalTime scheduledEndTime;

    @Schema(description = "읽기 전용 여부")
    private boolean readOnly;

    @Schema(description = "종료 버튼 상태")
    private ChatCloseActionStatus closeActionStatus;

    public static ChatRoomDetailResponse of(
            ChatRoom room,
            String partnerProfileImageUrl,
            String partnerName,
            ChatCloseActionStatus closeActionStatus
    ) {
        return ChatRoomDetailResponse.builder()
                .roomId(room.getChatRoomId())
                .partnerProfileImageUrl(partnerProfileImageUrl)
                .partnerName(partnerName)
                .method(room.getApplication().getSlot().getMentor().getMethod())
                .scheduledDate(room.getScheduledDate())
                .scheduledStartTime(room.getScheduledStartTime())
                .scheduledEndTime(room.getScheduledEndTime())
                .readOnly(room.getStatus() == ChatRoomStatus.READ_ONLY)
                .closeActionStatus(closeActionStatus)
                .build();
    }
}
