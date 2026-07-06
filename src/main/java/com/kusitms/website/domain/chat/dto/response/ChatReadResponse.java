package com.kusitms.website.domain.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "채팅 읽음 처리 응답")
public class ChatReadResponse {

    @Schema(description = "채팅방 ID")
    private Long roomId;

    @Schema(description = "읽음 처리된 메시지 수")
    private int updatedCount;
}
