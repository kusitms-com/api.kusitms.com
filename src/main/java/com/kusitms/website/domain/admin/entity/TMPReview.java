package com.kusitms.website.domain.admin.entity;

import com.kusitms.website.domain.project.entity.Team;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TMPReview {
    @Id
    @Column(name = "review_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Team team;

    @Column(nullable = false, length = 300)
    private String review;

    @Builder
    public TMPReview(String name, Team team, String review) {
        this.name = name;
        this.team = team;
        this.review = review;
    }

    public void update(String name, Team team, String review) {
        this.name = name;
        this.team = team;
        this.review = review;
    }
}
