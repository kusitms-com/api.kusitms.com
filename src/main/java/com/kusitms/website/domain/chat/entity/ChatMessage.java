package com.kusitms.website.domain.chat.entity;

import com.kusitms.website.domain.user.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    @Column(name = "chat_message_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatMessageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    @Column(columnDefinition = "TEXT", nullable = false, length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatMessageStatus status;

    @Column(nullable = false)
    private boolean readByRecipient;

    private LocalDateTime createdAt;

    @Builder
    public ChatMessage(ChatRoom chatRoom, Member sender, String content,
                       ChatMessageStatus status, boolean readByRecipient) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.content = content;
        this.status = status;
        this.readByRecipient = readByRecipient;
        this.createdAt = LocalDateTime.now();
    }

    public void markAsRead() {
        this.readByRecipient = true;
    }

    public void updateStatus(ChatMessageStatus status) {
        this.status = status;
    }
}
