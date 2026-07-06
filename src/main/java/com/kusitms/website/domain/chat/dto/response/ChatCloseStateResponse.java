package com.kusitms.website.domain.chat.dto.response;

import com.kusitms.website.domain.chat.entity.ChatCloseRequester;
import com.kusitms.website.domain.chat.entity.ChatRoom;
import com.kusitms.website.domain.chat.entity.ChatRoomStatus;
import com.kusitms.website.domain.mentoring.entity.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "채팅 종료 상태 응답")
public class ChatCloseStateResponse {

    @Schema(description = "채팅방 ID")
    private Long roomId;

    @Schema(description = "채팅방 상태")
    private ChatRoomStatus roomStatus;

    @Schema(description = "종료 요청자")
    private ChatCloseRequester closeRequester;

    @Schema(description = "멘토링 신청 상태")
    private ApplicationStatus applicationStatus;

    public static ChatCloseStateResponse from(ChatRoom room) {
        return new ChatCloseStateResponse(
                room.getChatRoomId(),
                room.getStatus(),
                room.getCloseRequester(),
                room.getApplication().getStatus()
        );
    }
}
