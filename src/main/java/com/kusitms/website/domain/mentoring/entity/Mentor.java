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
public class Mentor {

    @Id
    @Column(name = "mentor_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mentorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String title;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MentoringCategory category;

    @Column(nullable = false)
    private String experience;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MentoringMethod method;

    private Integer durationMinutes;

    @Column(nullable = false)
    private Integer pricePerHour;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    private boolean acceptingRequests;

    private boolean active;

    private LocalDateTime createdAt;

    @Builder
    public Mentor(Member member, String title, String profileImageUrl,
                  MentoringCategory category, String experience,
                  MentoringMethod method, Integer durationMinutes, Integer pricePerHour,
                  String introduction, boolean acceptingRequests, boolean active) {
        this.member = member;
        this.title = title;
        this.profileImageUrl = profileImageUrl;
        this.category = category;
        this.experience = experience;
        this.method = method;
        this.durationMinutes = durationMinutes;
        this.pricePerHour = pricePerHour;
        this.introduction = introduction;
        this.acceptingRequests = acceptingRequests;
        this.active = active;
        this.createdAt = LocalDateTime.now();
    }

    public void updateProfile(String title, String profileImageUrl, MentoringCategory category,
                              String experience, MentoringMethod method, Integer durationMinutes,
                              Integer pricePerHour, String introduction) {
        this.title = title;
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
        this.category = category;
        this.experience = experience;
        this.method = method;
        this.durationMinutes = durationMinutes;
        this.pricePerHour = pricePerHour;
        this.introduction = introduction;
    }

    public void updateAcceptingRequests(boolean acceptingRequests) {
        this.acceptingRequests = acceptingRequests;
    }

    public void updateVisibility(boolean visible) {
        this.active = visible;
    }

    public boolean isProfileCompleted() {
        return profileImageUrl != null && !profileImageUrl.isBlank()
                && title != null && !title.isBlank()
                && experience != null && !experience.isBlank()
                && introduction != null && !introduction.isBlank()
                && category != null
                && method != null
                && durationMinutes != null
                && pricePerHour != null;
    }

    public void deactivate() {
        this.active = false;
    }
}
