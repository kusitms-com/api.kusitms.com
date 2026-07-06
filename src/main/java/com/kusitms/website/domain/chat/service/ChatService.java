package com.kusitms.website.domain.chat.service;

import com.kusitms.website.domain.chat.dto.request.ChatMessageSendRequest;
import com.kusitms.website.domain.chat.dto.response.ChatCloseActionStatus;
import com.kusitms.website.domain.chat.dto.response.ChatMessageResponse;
import com.kusitms.website.domain.chat.dto.response.ChatMessageSliceResponse;
import com.kusitms.website.domain.chat.dto.response.ChatRoomDetailResponse;
import com.kusitms.website.domain.chat.dto.response.ChatRoomListItemResponse;
import com.kusitms.website.domain.chat.dto.response.ChatRoomListResponse;
import com.kusitms.website.domain.chat.entity.ChatCloseRequester;
import com.kusitms.website.domain.chat.entity.ChatMessage;
import com.kusitms.website.domain.chat.entity.ChatMessageStatus;
import com.kusitms.website.domain.chat.entity.ChatRoom;
import com.kusitms.website.domain.chat.entity.ChatRoomStatus;
import com.kusitms.website.domain.chat.repository.ChatMessageRepository;
import com.kusitms.website.domain.chat.repository.ChatRoomRepository;
import com.kusitms.website.domain.chat.repository.UnreadCountProjection;
import com.kusitms.website.domain.user.Member;
import com.kusitms.website.domain.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        ChatRoom room = getParticipatingRoom(userId, roomId);

        if (room.getStatus() == ChatRoomStatus.READ_ONLY) {
            throw new IllegalArgumentException("읽기 전용 채팅방에는 메시지를 전송할 수 없습니다.");
        }

        String normalizedContent = request.getContent().trim();
        if (normalizedContent.isEmpty()) {
            throw new IllegalArgumentException("메시지를 입력해 주세요.");
        }

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

        return ChatMessageResponse.from(message, userId);
    }

    private ChatRoom getParticipatingRoom(Long userId, Long roomId) {
        ChatRoom room = chatRoomRepository.findByChatRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));

        boolean isParticipant = room.getApplication().getApplicant().getUserId().equals(userId)
                || room.getApplication().getSlot().getMentor().getMember().getUserId().equals(userId);
        if (!isParticipant) {
            throw new IllegalArgumentException("해당 채팅방에 접근할 수 없습니다.");
        }

        return room;
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
}
