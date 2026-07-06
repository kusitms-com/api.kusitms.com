package com.kusitms.website.domain.chat.dto.response;

import com.kusitms.website.domain.chat.entity.ChatRoom;
import com.kusitms.website.domain.chat.entity.ChatRoomStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "채팅방 목록 아이템 응답")
public class ChatRoomListItemResponse {

    @Schema(description = "채팅방 ID")
    private Long roomId;

    @Schema(description = "상대방 프로필 이미지 URL")
    private String partnerProfileImageUrl;

    @Schema(description = "상대방 이름")
    private String partnerName;

    @Schema(description = "최근 메시지")
    private String lastMessage;

    @Schema(description = "최근 메시지 시각")
    private LocalDateTime lastMessageAt;

    @Schema(description = "미읽음 메시지 수")
    private long unreadCount;

    @Schema(description = "읽기 전용 여부")
    private boolean readOnly;

    public static ChatRoomListItemResponse of(ChatRoom room, String partnerProfileImageUrl, String partnerName, long unreadCount) {
        return ChatRoomListItemResponse.builder()
                .roomId(room.getChatRoomId())
                .partnerProfileImageUrl(partnerProfileImageUrl)
                .partnerName(partnerName)
                .lastMessage(room.getLastMessage())
                .lastMessageAt(room.getLastMessageAt())
                .unreadCount(unreadCount)
                .readOnly(room.getStatus() == ChatRoomStatus.READ_ONLY)
                .build();
    }
}
