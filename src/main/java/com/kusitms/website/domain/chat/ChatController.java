package com.kusitms.website.domain.chat;

import com.kusitms.website.domain.chat.dto.request.ChatMessageSendRequest;
import com.kusitms.website.domain.chat.dto.request.ChatScheduleUpdateRequest;
import com.kusitms.website.domain.chat.dto.response.ChatCloseApproveResponse;
import com.kusitms.website.domain.chat.dto.response.ChatCloseRequestResponse;
import com.kusitms.website.domain.chat.dto.response.ChatMessageResponse;
import com.kusitms.website.domain.chat.dto.response.ChatMessageSliceResponse;
import com.kusitms.website.domain.chat.dto.response.ChatReadResponse;
import com.kusitms.website.domain.chat.dto.response.ChatRoomDetailResponse;
import com.kusitms.website.domain.chat.dto.response.ChatRoomListResponse;
import com.kusitms.website.domain.chat.service.ChatService;
import com.kusitms.website.global.auth.UserPrincipal;
import com.kusitms.website.global.common.BaseResponse;
import javax.validation.Valid;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/rooms/{roomId}/messages")
    @Operation(summary = "채팅 메시지 목록 조회", description = "로그인한 사용자의 채팅 메시지를 커서 기반으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "400", description = "조회 불가"),
    })
    public ResponseEntity<BaseResponse<ChatMessageSliceResponse>> getChatMessages(
            @PathVariable Long roomId,
            @RequestParam(required = false) Long cursorId) {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(new BaseResponse<>(chatService.getChatMessages(userId, roomId, cursorId)));
    }

    @PostMapping("/rooms/{roomId}/messages")
    @Operation(summary = "채팅 메시지 전송", description = "로그인한 사용자가 채팅방에 메시지를 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전송 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "400", description = "전송 불가"),
    })
    public ResponseEntity<BaseResponse<ChatMessageResponse>> sendMessage(
            @PathVariable Long roomId,
            @RequestBody @Valid ChatMessageSendRequest request) {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(new BaseResponse<>(chatService.sendMessage(userId, roomId, request)));
    }

    @PostMapping("/rooms/{roomId}/read")
    @Operation(summary = "채팅 읽음 처리", description = "로그인한 사용자가 채팅방의 미읽음 메시지를 모두 읽음 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "400", description = "처리 불가"),
    })
    public ResponseEntity<BaseResponse<ChatReadResponse>> markMessagesAsRead(@PathVariable Long roomId) {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(new BaseResponse<>(chatService.markMessagesAsRead(userId, roomId)));
    }

    @PostMapping("/rooms/{roomId}/close-request")
    @Operation(summary = "채팅 종료 요청", description = "로그인한 사용자가 채팅 종료를 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "400", description = "요청 불가"),
    })
    public ResponseEntity<BaseResponse<ChatCloseRequestResponse>> requestCloseChatRoom(@PathVariable Long roomId) {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(new BaseResponse<>(chatService.requestCloseChatRoom(userId, roomId)));
    }

    @PostMapping("/rooms/{roomId}/close-approve")
    @Operation(summary = "채팅 종료 승인", description = "상대방의 채팅 종료 요청을 승인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "승인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "400", description = "승인 불가"),
    })
    public ResponseEntity<BaseResponse<ChatCloseApproveResponse>> approveCloseChatRoom(@PathVariable Long roomId) {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(new BaseResponse<>(chatService.approveCloseChatRoom(userId, roomId)));
    }

    @PutMapping("/rooms/{roomId}/schedule")
    @Operation(summary = "채팅 일정 수정", description = "OB 멘토가 채팅방의 멘토링 일정을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "400", description = "수정 불가"),
    })
    public ResponseEntity<BaseResponse<ChatRoomDetailResponse>> updateSchedule(
            @PathVariable Long roomId,
            @RequestBody @Valid ChatScheduleUpdateRequest request) {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(new BaseResponse<>(chatService.updateSchedule(userId, roomId, request)));
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
