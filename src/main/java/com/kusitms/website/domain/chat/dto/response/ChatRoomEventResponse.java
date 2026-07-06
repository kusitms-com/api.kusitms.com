package com.kusitms.website.domain.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "채팅 실시간 이벤트 응답")
public class ChatRoomEventResponse {

    @Schema(description = "이벤트 타입")
    private ChatEventType eventType;

    @Schema(description = "채팅방 ID")
    private Long roomId;

    @Schema(description = "이벤트 데이터")
    private Object payload;
}
