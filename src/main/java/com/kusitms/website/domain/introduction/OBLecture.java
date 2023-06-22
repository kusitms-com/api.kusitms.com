package com.kusitms.website.domain.introduction;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OBLecture {
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

    private String topic;

    @Builder
    public OBLecture(Introduction introduction, String imageUrl,
                         String name, String topic) {
        this.introduction = introduction;
        introduction.getObLecture().add(this);
        this.imageUrl = imageUrl;
        this.name = name;
        this.topic = topic;
    }
}
