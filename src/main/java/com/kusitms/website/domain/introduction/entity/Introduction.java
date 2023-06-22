package com.kusitms.website.domain.introduction.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Introduction {
    @Id
    @Column(name = "introduction_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long introductionId;

    // 배너
    @Column(name = "banner_cardinal")
    private Long bannerCardinal;
    @Column(name = "banner_status")
    @Enumerated(EnumType.STRING)
    private BannerStatus bannerStatus;

    // 학회 정보
    @Column(name = "member_count")
    private Long memberCount;
    @Column(name = "project_count")
    private Long projectCount;
    @Column(name = "university_count")
    private Long universityCount;

    // 학회 소개 영상
    @Column(name = "intro_youtube_link")
    private String introYoutubeLink;

    // 파트너사 이미지
    @Column(name = "partner_image_url")
    private String partnerImageUrl;

    // 운영진 소개
    @OneToMany(mappedBy = "introduction", cascade = CascadeType.ALL)
    private List<ManageTeam> manageTeam = new ArrayList<>();

    // 전문가 초청 강연자
    @OneToMany(mappedBy = "introduction", cascade = CascadeType.ALL)
    private List<ExpertLecture> expertLecture = new ArrayList<>();

    // OB 초청 강연자
    @OneToMany(mappedBy = "introduction", cascade = CascadeType.ALL)
    private List<OBLecture> obLecture = new ArrayList<>();


    @Builder
    public Introduction(Long bannerCardinal, BannerStatus bannerStatus,
                        Long memberCount, Long projectCount, Long universityCount,
                        String partnerImageUrl, String introYoutubeLink)
    {
        this.bannerCardinal = bannerCardinal;
        this.bannerStatus = bannerStatus;
        this.memberCount = memberCount;
        this.projectCount = projectCount;
        this.universityCount = universityCount;
        this.partnerImageUrl = partnerImageUrl;
        this.introYoutubeLink = introYoutubeLink;
    }
}
