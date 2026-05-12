package com.kusitms.website.domain.introduction.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sponsor {
    @Id
    @Column(name = "sponsor_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sponsorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "introduction_id")
    private Introduction introduction;

    @Column(name = "image_url")
    private String imageUrl;

    @Builder
    public Sponsor(Introduction introduction, String imageUrl) {
        this.introduction = introduction;
        this.imageUrl = imageUrl;
    }
}