package com.kusitms.website.domain.mentoring.entity;

import com.kusitms.website.domain.user.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MentoringReview {

    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member reviewer;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MentoringReviewKeyword> keywords = new ArrayList<>();

    @Builder
    public MentoringReview(Mentor mentor, Member reviewer, String content) {
        this.mentor = mentor;
        this.reviewer = reviewer;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public void addKeyword(MentoringReviewKeyword keyword) {
        this.keywords.add(keyword);
    }
}
