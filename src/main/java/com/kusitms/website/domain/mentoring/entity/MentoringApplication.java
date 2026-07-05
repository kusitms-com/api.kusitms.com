package com.kusitms.website.domain.mentoring.entity;

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
public class MentoringApplication {

    @Id
    @Column(name = "application_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private MentoringSlot slot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member applicant;

    @Column(length = 500)
    private String message;

    @Column(length = 300)
    private String rejectionReason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    private LocalDateTime createdAt;

    @Builder
    public MentoringApplication(MentoringSlot slot, Member applicant,
                                String message, ApplicationStatus status) {
        this.slot = slot;
        this.applicant = applicant;
        this.message = message;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public void updateStatus(ApplicationStatus status) {
        this.status = status;
    }

    public void approve() {
        this.status = ApplicationStatus.ACTIVE;
    }

    public void complete() {
        this.status = ApplicationStatus.COMPLETED;
    }

    public void reject(String rejectionReason) {
        this.status = ApplicationStatus.REJECTED;
        this.rejectionReason = rejectionReason;
    }
}
