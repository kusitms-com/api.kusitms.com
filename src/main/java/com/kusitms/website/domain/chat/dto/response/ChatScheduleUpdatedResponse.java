package com.kusitms.website.domain.chat.dto.response;

import com.kusitms.website.domain.chat.entity.ChatRoom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@Schema(description = "채팅 일정 수정 이벤트 응답")
public class ChatScheduleUpdatedResponse {

    @Schema(description = "채팅방 ID")
    private Long roomId;

    @Schema(description = "멘토링 날짜")
    private LocalDate scheduledDate;

    @Schema(description = "멘토링 시작 시간")
    private LocalTime scheduledStartTime;

    @Schema(description = "멘토링 종료 시간")
    private LocalTime scheduledEndTime;

    public static ChatScheduleUpdatedResponse from(ChatRoom room) {
        return new ChatScheduleUpdatedResponse(
                room.getChatRoomId(),
                room.getScheduledDate(),
                room.getScheduledStartTime(),
                room.getScheduledEndTime()
        );
    }
}
