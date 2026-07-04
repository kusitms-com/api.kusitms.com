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

    @Column(nullable = false)
    private Integer pricePerHour;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    private boolean active;

    private LocalDateTime createdAt;

    @Builder
    public Mentor(Member member, String title, String profileImageUrl,
                  MentoringCategory category, String experience,
                  MentoringMethod method, Integer pricePerHour,
                  String introduction, boolean active) {
        this.member = member;
        this.title = title;
        this.profileImageUrl = profileImageUrl;
        this.category = category;
        this.experience = experience;
        this.method = method;
        this.pricePerHour = pricePerHour;
        this.introduction = introduction;
        this.active = active;
        this.createdAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
    }
}
