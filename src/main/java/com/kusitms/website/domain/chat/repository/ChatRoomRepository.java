package com.kusitms.website.domain.chat.repository;

import com.kusitms.website.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @EntityGraph(attributePaths = {
            "application",
            "application.slot",
            "application.slot.mentor",
            "application.slot.mentor.member",
            "application.applicant"
    })
    Optional<ChatRoom> findByChatRoomId(Long chatRoomId);

    @EntityGraph(attributePaths = {
            "application",
            "application.slot",
            "application.slot.mentor",
            "application.slot.mentor.member",
            "application.applicant"
    })
    Optional<ChatRoom> findByApplicationApplicationId(Long applicationId);

    @EntityGraph(attributePaths = {
            "application",
            "application.slot",
            "application.slot.mentor",
            "application.slot.mentor.member",
            "application.applicant"
    })
    @Query("SELECT cr FROM ChatRoom cr " +
            "WHERE cr.application.applicant.userId = :userId " +
            "OR cr.application.slot.mentor.member.userId = :userId " +
            "ORDER BY COALESCE(cr.lastMessageAt, cr.createdAt) DESC, cr.createdAt DESC")
    List<ChatRoom> findAllByParticipantUserIdOrderByRecentMessage(@Param("userId") Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.chatRoomId = :chatRoomId")
    Optional<ChatRoom> findByChatRoomIdWithLock(@Param("chatRoomId") Long chatRoomId);
}
