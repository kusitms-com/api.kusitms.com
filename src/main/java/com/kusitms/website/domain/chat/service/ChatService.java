package com.kusitms.website.domain.chat.service;

import com.kusitms.website.domain.chat.dto.request.ChatMessageSendRequest;
import com.kusitms.website.domain.chat.dto.request.ChatScheduleUpdateRequest;
import com.kusitms.website.domain.chat.dto.response.ChatCloseActionStatus;
import com.kusitms.website.domain.chat.dto.response.ChatCloseApproveResponse;
import com.kusitms.website.domain.chat.dto.response.ChatCloseRequestResponse;
import com.kusitms.website.domain.chat.dto.response.ChatCloseStateResponse;
import com.kusitms.website.domain.chat.dto.response.ChatEventType;
import com.kusitms.website.domain.chat.dto.response.ChatMessageResponse;
import com.kusitms.website.domain.chat.dto.response.ChatReadResponse;
import com.kusitms.website.domain.chat.dto.response.ChatMessageSliceResponse;
import com.kusitms.website.domain.chat.dto.response.ChatRoomEventResponse;
import com.kusitms.website.domain.chat.dto.response.ChatRoomDetailResponse;
import com.kusitms.website.domain.chat.dto.response.ChatRoomListItemResponse;
import com.kusitms.website.domain.chat.dto.response.ChatRoomListResponse;
import com.kusitms.website.domain.chat.dto.response.ChatScheduleUpdatedResponse;
import com.kusitms.website.domain.chat.entity.ChatCloseRequester;
import com.kusitms.website.domain.chat.entity.ChatMessage;
import com.kusitms.website.domain.chat.entity.ChatMessageStatus;
import com.kusitms.website.domain.chat.entity.ChatRoom;
import com.kusitms.website.domain.chat.entity.ChatRoomStatus;
import com.kusitms.website.domain.chat.repository.ChatMessageRepository;
import com.kusitms.website.domain.chat.repository.ChatRoomRepository;
import com.kusitms.website.domain.chat.repository.UnreadCountProjection;
import com.kusitms.website.domain.mentoring.entity.ApplicationStatus;
import com.kusitms.website.domain.user.Member;
import com.kusitms.website.domain.user.MemberRole;
import com.kusitms.website.domain.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private static final int MESSAGE_PAGE_SIZE = 50;

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatRoomListResponse getChatRooms(Long userId) {
        List<ChatRoom> rooms = chatRoomRepository.findAllByParticipantUserIdOrderByRecentMessage(userId);
        Map<Long, Long> unreadCountMap = getUnreadCountMap(rooms, userId);

        List<ChatRoomListItemResponse> responses = rooms.stream()
                .map(room -> ChatRoomListItemResponse.of(
                        room,
                        getPartnerProfileImageUrl(room, userId),
                        getPartnerName(room, userId),
                        unreadCountMap.getOrDefault(room.getChatRoomId(), 0L)
                ))
                .collect(Collectors.toList());

        return ChatRoomListResponse.builder()
                .rooms(responses)
                .build();
    }

    public ChatRoomDetailResponse getChatRoomDetail(Long userId, Long roomId) {
        ChatRoom room = getParticipatingRoom(userId, roomId);

        return ChatRoomDetailResponse.of(
                room,
                getPartnerProfileImageUrl(room, userId),
                getPartnerName(room, userId),
                resolveCloseActionStatus(room, userId)
        );
    }

    public ChatMessageSliceResponse getChatMessages(Long userId, Long roomId, Long cursorId) {
        ChatRoom room = getParticipatingRoom(userId, roomId);

        Slice<ChatMessage> messageSlice;
        if (cursorId == null) {
            messageSlice = chatMessageRepository.findByChatRoomChatRoomIdOrderByChatMessageIdDesc(
                    room.getChatRoomId(), PageRequest.of(0, MESSAGE_PAGE_SIZE));
        } else {
            messageSlice = chatMessageRepository.findByChatRoomChatRoomIdAndChatMessageIdLessThanOrderByChatMessageIdDesc(
                    room.getChatRoomId(), cursorId, PageRequest.of(0, MESSAGE_PAGE_SIZE));
        }

        List<ChatMessageResponse> messages = messageSlice.getContent().stream()
                .sorted(Comparator.comparing(ChatMessage::getChatMessageId))
                .map(message -> ChatMessageResponse.from(message, userId))
                .collect(Collectors.toList());

        Long nextCursorId = messageSlice.hasNext() && !messageSlice.getContent().isEmpty()
                ? messageSlice.getContent().get(messageSlice.getContent().size() - 1).getChatMessageId()
                : null;

        return ChatMessageSliceResponse.builder()
                .messages(messages)
                .nextCursorId(nextCursorId)
                .hasNext(messageSlice.hasNext())
                .build();
    }

    @Transactional
    public ChatMessageResponse sendMessage(Long userId, Long roomId, ChatMessageSendRequest request) {
        ChatRoom room = getParticipatingRoomWithLock(userId, roomId);

        if (room.getStatus() == ChatRoomStatus.READ_ONLY) {
            throw new IllegalArgumentException("읽기 전용 채팅방에는 메시지를 전송할 수 없습니다.");
        }

        String normalizedContent = validateAndNormalizeMessageContent(request);

        Member sender = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        ChatMessage message = chatMessageRepository.save(ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .content(normalizedContent)
                .status(ChatMessageStatus.SENT)
                .readByRecipient(false)
                .build());

        room.updateLastMessage(message.getContent(), message.getCreatedAt());

        ChatMessageResponse response = ChatMessageResponse.from(message, userId);
        publishRoomEvent(room.getChatRoomId(), ChatEventType.MESSAGE_SENT, response);
        publishRoomListUpdates(room);
        return response;
    }

    @Transactional
    public ChatReadResponse markMessagesAsRead(Long userId, Long roomId) {
        ChatRoom room = getParticipatingRoom(userId, roomId);
        int updatedCount = chatMessageRepository.markAllAsRead(room.getChatRoomId(), userId);
        ChatReadResponse response = new ChatReadResponse(room.getChatRoomId(), updatedCount);
        publishRoomEvent(room.getChatRoomId(), ChatEventType.MESSAGE_READ, response);
        publishRoomListUpdates(room);
        return response;
    }

    @Transactional
    public ChatCloseRequestResponse requestCloseChatRoom(Long userId, Long roomId) {
        ChatRoom room = getParticipatingRoomWithLock(userId, roomId);

        if (room.getStatus() == ChatRoomStatus.READ_ONLY) {
            throw new IllegalArgumentException("이미 종료된 채팅방입니다.");
        }
        if (room.getCloseRequester() != ChatCloseRequester.NONE) {
            throw new IllegalArgumentException("이미 종료 요청이 진행 중인 채팅방입니다.");
        }

        room.requestClose(resolveCloseRequester(room, userId));

        ChatCloseRequestResponse response = new ChatCloseRequestResponse(
                room.getChatRoomId(),
                resolveCloseActionStatus(room, userId)
        );
        publishRoomEvent(room.getChatRoomId(), ChatEventType.CLOSE_REQUESTED, ChatCloseStateResponse.from(room));
        return response;
    }

    @Transactional
    public ChatCloseApproveResponse approveCloseChatRoom(Long userId, Long roomId) {
        ChatRoom room = getParticipatingRoomWithLock(userId, roomId);

        if (room.getStatus() == ChatRoomStatus.READ_ONLY) {
            throw new IllegalArgumentException("이미 종료된 채팅방입니다.");
        }
        if (room.getCloseRequester() == ChatCloseRequester.NONE) {
            throw new IllegalArgumentException("종료 요청이 없는 채팅방입니다.");
        }
        if (room.getCloseRequester() == resolveCloseRequester(room, userId)) {
            throw new IllegalArgumentException("내가 요청한 종료는 직접 승인할 수 없습니다.");
        }

        room.getApplication().complete();
        room.updateStatus(ChatRoomStatus.READ_ONLY);
        room.clearCloseRequest();

        ChatCloseApproveResponse response = new ChatCloseApproveResponse(
                room.getChatRoomId(),
                room.getStatus(),
                room.getApplication().getStatus(),
                resolveCloseActionStatus(room, userId)
        );
        publishRoomEvent(room.getChatRoomId(), ChatEventType.CLOSE_APPROVED, ChatCloseStateResponse.from(room));
        publishRoomListUpdates(room);
        return response;
    }

    @Transactional
    public ChatRoomDetailResponse updateSchedule(Long userId, Long roomId, ChatScheduleUpdateRequest request) {
        ChatRoom room = getParticipatingRoomWithLock(userId, roomId);

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (member.getRole() != MemberRole.OB) {
            throw new IllegalArgumentException("OB 회원만 일정을 수정할 수 있습니다.");
        }
        if (!room.getApplication().getSlot().getMentor().getMember().getUserId().equals(userId)) {
            throw new IllegalArgumentException("멘토만 일정을 수정할 수 있습니다.");
        }
        if (room.getStatus() == ChatRoomStatus.READ_ONLY) {
            throw new IllegalArgumentException("읽기 전용 채팅방의 일정은 수정할 수 없습니다.");
        }

        validateScheduleRequest(request);

        LocalDateTime scheduledDateTime = LocalDateTime.of(
                request.getScheduledDate(),
                request.getScheduledStartTime()
        );
        if (!scheduledDateTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("현재 이후의 일정을 입력해 주세요.");
        }
        if (!request.getScheduledEndTime().isAfter(request.getScheduledStartTime())) {
            throw new IllegalArgumentException("종료 시간은 시작 시간보다 이후여야 합니다.");
        }

        room.updateSchedule(
                request.getScheduledDate(),
                request.getScheduledStartTime(),
                request.getScheduledEndTime()
        );

        ChatRoomDetailResponse response = ChatRoomDetailResponse.of(
                room,
                getPartnerProfileImageUrl(room, userId),
                getPartnerName(room, userId),
                resolveCloseActionStatus(room, userId)
        );
        publishRoomEvent(room.getChatRoomId(), ChatEventType.SCHEDULE_UPDATED, ChatScheduleUpdatedResponse.from(room));
        return response;
    }

    private ChatRoom getParticipatingRoom(Long userId, Long roomId) {
        ChatRoom room = chatRoomRepository.findByChatRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
        validateParticipant(room, userId);
        return room;
    }

    private ChatRoom getParticipatingRoomWithLock(Long userId, Long roomId) {
        ChatRoom room = chatRoomRepository.findByChatRoomIdWithLock(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
        validateParticipant(room, userId);
        return room;
    }

    private void validateParticipant(ChatRoom room, Long userId) {
        boolean isParticipant = room.getApplication().getApplicant().getUserId().equals(userId)
                || room.getApplication().getSlot().getMentor().getMember().getUserId().equals(userId);
        if (!isParticipant) {
            throw new IllegalArgumentException("해당 채팅방에 접근할 수 없습니다.");
        }
    }

    private Map<Long, Long> getUnreadCountMap(List<ChatRoom> rooms, Long userId) {
        if (rooms.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> roomIds = rooms.stream()
                .map(ChatRoom::getChatRoomId)
                .collect(Collectors.toList());

        return chatMessageRepository.countUnreadMessagesByRoomIds(roomIds, userId).stream()
                .collect(Collectors.toMap(
                        UnreadCountProjection::getChatRoomId,
                        UnreadCountProjection::getUnreadCount
                ));
    }

    private String getPartnerProfileImageUrl(ChatRoom room, Long userId) {
        if (room.getApplication().getApplicant().getUserId().equals(userId)) {
            return room.getApplication().getSlot().getMentor().getMember().getProfileImageUrl();
        }
        return room.getApplication().getApplicant().getProfileImageUrl();
    }

    private String getPartnerName(ChatRoom room, Long userId) {
        if (room.getApplication().getApplicant().getUserId().equals(userId)) {
            return room.getApplication().getSlot().getMentor().getMember().getName();
        }
        return room.getApplication().getApplicant().getName();
    }

    private ChatCloseActionStatus resolveCloseActionStatus(ChatRoom room, Long userId) {
        if (room.getStatus() == ChatRoomStatus.READ_ONLY) {
            return ChatCloseActionStatus.HIDDEN;
        }

        boolean isMentee = room.getApplication().getApplicant().getUserId().equals(userId);

        if (room.getCloseRequester() == ChatCloseRequester.NONE) {
            return ChatCloseActionStatus.REQUEST;
        }

        if ((isMentee && room.getCloseRequester() == ChatCloseRequester.MENTEE)
                || (!isMentee && room.getCloseRequester() == ChatCloseRequester.MENTOR)) {
            return ChatCloseActionStatus.REQUEST_PENDING;
        }

        return ChatCloseActionStatus.APPROVE;
    }

    private ChatCloseRequester resolveCloseRequester(ChatRoom room, Long userId) {
        if (room.getApplication().getApplicant().getUserId().equals(userId)) {
            return ChatCloseRequester.MENTEE;
        }
        return ChatCloseRequester.MENTOR;
    }

    private String validateAndNormalizeMessageContent(ChatMessageSendRequest request) {
        if (request == null || request.getContent() == null) {
            throw new IllegalArgumentException("메시지를 입력해 주세요.");
        }

        String normalizedContent = request.getContent().trim();
        if (normalizedContent.isEmpty()) {
            throw new IllegalArgumentException("메시지를 입력해 주세요.");
        }
        if (normalizedContent.length() > 1000) {
            throw new IllegalArgumentException("메시지는 1000자 이하여야 합니다.");
        }
        return normalizedContent;
    }

    private void validateScheduleRequest(ChatScheduleUpdateRequest request) {
        if (request == null
                || request.getScheduledDate() == null
                || request.getScheduledStartTime() == null
                || request.getScheduledEndTime() == null) {
            throw new IllegalArgumentException("변경 일정은 날짜와 시간을 모두 입력해 주세요.");
        }
    }

    private void publishRoomEvent(Long roomId, ChatEventType eventType, Object payload) {
        messagingTemplate.convertAndSend(
                "/sub/chat/rooms/" + roomId,
                new ChatRoomEventResponse(eventType, roomId, payload)
        );
    }

    private void publishRoomListUpdates(ChatRoom room) {
        Long applicantUserId = room.getApplication().getApplicant().getUserId();
        Long mentorUserId = room.getApplication().getSlot().getMentor().getMember().getUserId();

        messagingTemplate.convertAndSend(
                "/sub/chat/users/" + applicantUserId + "/rooms",
                new ChatRoomEventResponse(
                        ChatEventType.ROOM_LIST_UPDATED,
                        room.getChatRoomId(),
                        buildChatRoomListItem(room, applicantUserId)
                )
        );

        messagingTemplate.convertAndSend(
                "/sub/chat/users/" + mentorUserId + "/rooms",
                new ChatRoomEventResponse(
                        ChatEventType.ROOM_LIST_UPDATED,
                        room.getChatRoomId(),
                        buildChatRoomListItem(room, mentorUserId)
                )
        );
    }

    private ChatRoomListItemResponse buildChatRoomListItem(ChatRoom room, Long userId) {
        long unreadCount = chatMessageRepository.countUnreadMessages(room.getChatRoomId(), userId);
        return ChatRoomListItemResponse.of(
                room,
                getPartnerProfileImageUrl(room, userId),
                getPartnerName(room, userId),
                unreadCount
        );
    }
}
