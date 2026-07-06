package com.kusitms.website.domain.chat;

import com.kusitms.website.domain.chat.dto.request.ChatMessageSendRequest;
import com.kusitms.website.domain.chat.dto.request.ChatScheduleUpdateRequest;
import com.kusitms.website.domain.chat.service.ChatService;
import com.kusitms.website.global.auth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.core.Authentication;

@Controller
@Validated
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatService chatService;

    @MessageMapping("/chat/rooms/{roomId}/messages")
    public void sendMessage(
            @DestinationVariable Long roomId,
            ChatMessageSendRequest request,
            Authentication authentication
    ) {
        chatService.sendMessage(getAuthenticatedUserId(authentication), roomId, request);
    }

    @MessageMapping("/chat/rooms/{roomId}/read")
    public void markMessagesAsRead(@DestinationVariable Long roomId, Authentication authentication) {
        chatService.markMessagesAsRead(getAuthenticatedUserId(authentication), roomId);
    }

    @MessageMapping("/chat/rooms/{roomId}/close-request")
    public void requestClose(@DestinationVariable Long roomId, Authentication authentication) {
        chatService.requestCloseChatRoom(getAuthenticatedUserId(authentication), roomId);
    }

    @MessageMapping("/chat/rooms/{roomId}/close-approve")
    public void approveClose(@DestinationVariable Long roomId, Authentication authentication) {
        chatService.approveCloseChatRoom(getAuthenticatedUserId(authentication), roomId);
    }

    @MessageMapping("/chat/rooms/{roomId}/schedule")
    public void updateSchedule(
            @DestinationVariable Long roomId,
            ChatScheduleUpdateRequest request,
            Authentication authentication
    ) {
        chatService.updateSchedule(getAuthenticatedUserId(authentication), roomId, request);
    }

    private Long getAuthenticatedUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return principal.getPk();
    }
}
