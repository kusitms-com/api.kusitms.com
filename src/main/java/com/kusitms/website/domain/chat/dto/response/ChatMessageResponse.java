package com.kusitms.website.domain.chat.dto.response;

import com.kusitms.website.domain.chat.entity.ChatMessage;
import com.kusitms.website.domain.chat.entity.ChatMessageStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "채팅 메시지 응답")
public class ChatMessageResponse {

    @Schema(description = "메시지 ID")
    private Long messageId;

    @Schema(description = "발신자 ID")
    private Long senderId;

    @Schema(description = "발신자 이름")
    private String senderName;

    @Schema(description = "내가 보낸 메시지 여부")
    private boolean mine;

    @Schema(description = "메시지 내용")
    private String content;

    @Schema(description = "메시지 상태")
    private ChatMessageStatus status;

    @Schema(description = "상대방 읽음 여부")
    private boolean readByRecipient;

    @Schema(description = "전송 시각")
    private LocalDateTime createdAt;

    public static ChatMessageResponse from(ChatMessage message, Long userId) {
        return ChatMessageResponse.builder()
                .messageId(message.getChatMessageId())
                .senderId(message.getSender().getUserId())
                .senderName(message.getSender().getName())
                .mine(message.getSender().getUserId().equals(userId))
                .content(message.getContent())
                .status(message.getStatus())
                .readByRecipient(message.isReadByRecipient())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
