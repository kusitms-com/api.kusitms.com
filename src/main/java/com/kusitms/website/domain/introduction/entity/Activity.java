package com.kusitms.website.domain.introduction.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Activity {
    @Id
    @Column(name = "activity_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "introduction_id")
    private Introduction introduction;

    @Column(nullable = false)
    private String name;

    @Column(name = "image_url1")
    private String imageUrl1;

    @Column(name = "image_url2")
    private String imageUrl2;

    @Column(length = 500)
    private String description;

    // 요청 순서대로 노출하기 위한 정렬값. 이 컬럼이 생기기 전 행은 null이라 id 순으로 정렬된다
    @Column(name = "display_order")
    private Integer displayOrder;

    @Builder
    public Activity(Introduction introduction, String name, String imageUrl1,
                    String imageUrl2, String description, Integer displayOrder) {
        this.introduction = introduction;
        this.name = name;
        this.imageUrl1 = imageUrl1;
        this.imageUrl2 = imageUrl2;
        this.description = description;
        this.displayOrder = displayOrder;
    }

    public void update(String name, String description, String imageUrl1, String imageUrl2, Integer displayOrder) {
        this.name = name;
        this.description = description;
        this.imageUrl1 = imageUrl1;
        this.imageUrl2 = imageUrl2;
        this.displayOrder = displayOrder;
    }
}