package com.kusitms.website.domain.chat.entity;

import com.kusitms.website.domain.mentoring.entity.MentoringApplication;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @Column(name = "chat_room_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private MentoringApplication application;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRoomStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatCloseRequester closeRequester;

    @Column(nullable = false)
    private LocalDate scheduledDate;

    @Column(nullable = false)
    private LocalTime scheduledStartTime;

    @Column(nullable = false)
    private LocalTime scheduledEndTime;

    @Column(length = 1000)
    private String lastMessage;

    private LocalDateTime lastMessageAt;

    private LocalDateTime createdAt;

    @Builder
    public ChatRoom(MentoringApplication application, ChatRoomStatus status,
                    ChatCloseRequester closeRequester, LocalDate scheduledDate,
                    LocalTime scheduledStartTime, LocalTime scheduledEndTime) {
        this.application = application;
        this.status = status;
        this.closeRequester = closeRequester;
        this.scheduledDate = scheduledDate;
        this.scheduledStartTime = scheduledStartTime;
        this.scheduledEndTime = scheduledEndTime;
        this.createdAt = LocalDateTime.now();
    }

    public void updateLastMessage(String lastMessage, LocalDateTime lastMessageAt) {
        this.lastMessage = lastMessage;
        this.lastMessageAt = lastMessageAt;
    }

    public void requestClose(ChatCloseRequester closeRequester) {
        this.closeRequester = closeRequester;
    }

    public void clearCloseRequest() {
        this.closeRequester = ChatCloseRequester.NONE;
    }

    public void updateStatus(ChatRoomStatus status) {
        this.status = status;
    }

    public void updateSchedule(LocalDate scheduledDate, LocalTime scheduledStartTime, LocalTime scheduledEndTime) {
        this.scheduledDate = scheduledDate;
        this.scheduledStartTime = scheduledStartTime;
        this.scheduledEndTime = scheduledEndTime;
    }
}
