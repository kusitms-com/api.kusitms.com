package com.kusitms.website.domain.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "채팅방 목록 조회 응답")
public class ChatRoomListResponse {

    @Schema(description = "채팅방 목록")
    private List<ChatRoomListItemResponse> rooms;
}
