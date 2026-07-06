package com.kusitms.website.domain.chat.repository;

import com.kusitms.website.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @EntityGraph(attributePaths = {"sender"})
    Slice<ChatMessage> findByChatRoomChatRoomIdOrderByChatMessageIdDesc(Long chatRoomId, Pageable pageable);

    @EntityGraph(attributePaths = {"sender"})
    Slice<ChatMessage> findByChatRoomChatRoomIdAndChatMessageIdLessThanOrderByChatMessageIdDesc(
            Long chatRoomId, Long cursorId, Pageable pageable);

    @EntityGraph(attributePaths = {"sender"})
    Optional<ChatMessage> findTopByChatRoomChatRoomIdOrderByChatMessageIdDesc(Long chatRoomId);

    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
            "WHERE cm.chatRoom.chatRoomId = :chatRoomId " +
            "AND cm.sender.userId <> :userId " +
            "AND cm.readByRecipient = false")
    long countUnreadMessages(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

    @Query("SELECT cm.chatRoom.chatRoomId AS chatRoomId, COUNT(cm) AS unreadCount " +
            "FROM ChatMessage cm " +
            "WHERE cm.chatRoom.chatRoomId IN :chatRoomIds " +
            "AND cm.sender.userId <> :userId " +
            "AND cm.readByRecipient = false " +
            "GROUP BY cm.chatRoom.chatRoomId")
    List<UnreadCountProjection> countUnreadMessagesByRoomIds(
            @Param("chatRoomIds") List<Long> chatRoomIds,
            @Param("userId") Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ChatMessage cm " +
            "SET cm.readByRecipient = true " +
            "WHERE cm.chatRoom.chatRoomId = :chatRoomId " +
            "AND cm.sender.userId <> :userId " +
            "AND cm.readByRecipient = false")
    int markAllAsRead(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);
}
