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

    @Builder
    public Activity(Introduction introduction, String name, String imageUrl1,
                    String imageUrl2, String description) {
        this.introduction = introduction;
        this.name = name;
        this.imageUrl1 = imageUrl1;
        this.imageUrl2 = imageUrl2;
        this.description = description;
    }
}