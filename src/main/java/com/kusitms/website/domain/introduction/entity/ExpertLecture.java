package com.kusitms.website.domain.introduction.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpertLecture {
    @Id
    @Column(name = "lecture_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lectureId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "introduction_id")
    private Introduction introduction;

    @Column(name = "image_url")
    private String imageUrl;

    private String name;

    private String corporation;

    private String description;

    @Builder
    public ExpertLecture(Introduction introduction, String imageUrl,
                    String name, String corporation, String description) {
        this.introduction = introduction;
        introduction.getExpertLecture().add(this);
        this.imageUrl = imageUrl;
        this.name = name;
        this.corporation = corporation;
        this.description = description;
    }

}
