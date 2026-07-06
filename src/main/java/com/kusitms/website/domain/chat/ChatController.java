package com.kusitms.website.domain.chat;

import com.kusitms.website.domain.chat.dto.response.ChatRoomDetailResponse;
import com.kusitms.website.domain.chat.dto.response.ChatRoomListResponse;
import com.kusitms.website.domain.chat.service.ChatService;
import com.kusitms.website.global.auth.UserPrincipal;
import com.kusitms.website.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "채팅 API")
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/rooms")
    @Operation(summary = "채팅방 목록 조회", description = "로그인한 사용자의 채팅방 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
    })
    public ResponseEntity<BaseResponse<ChatRoomListResponse>> getChatRooms() {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(new BaseResponse<>(chatService.getChatRooms(userId)));
    }

    @GetMapping("/rooms/{roomId}")
    @Operation(summary = "채팅방 상세 조회", description = "로그인한 사용자의 채팅방 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "400", description = "조회 불가"),
    })
    public ResponseEntity<BaseResponse<ChatRoomDetailResponse>> getChatRoomDetail(@PathVariable Long roomId) {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(new BaseResponse<>(chatService.getChatRoomDetail(userId, roomId)));
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return principal.getPk();
    }
}
