package com.kusitms.website.domain.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "채팅 메시지 목록 조회 응답")
public class ChatMessageSliceResponse {

    @Schema(description = "메시지 목록")
    private List<ChatMessageResponse> messages;

    @Schema(description = "다음 커서 ID")
    private Long nextCursorId;

    @Schema(description = "이전 메시지 추가 조회 가능 여부")
    private boolean hasNext;
}
