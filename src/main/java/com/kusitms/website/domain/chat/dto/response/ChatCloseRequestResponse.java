package com.kusitms.website.domain.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "채팅 종료 요청 응답")
public class ChatCloseRequestResponse {

    @Schema(description = "채팅방 ID")
    private Long roomId;

    @Schema(description = "종료 버튼 상태")
    private ChatCloseActionStatus closeActionStatus;
}
