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

    // 요청 순서대로 노출하기 위한 정렬값. 이 컬럼이 생기기 전 행은 null이라 id 순으로 정렬된다
    @Column(name = "display_order")
    private Integer displayOrder;

    @Builder
    public Sponsor(Introduction introduction, String imageUrl, Integer displayOrder) {
        this.introduction = introduction;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
    }

    public void update(String imageUrl, Integer displayOrder) {
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
    }
}