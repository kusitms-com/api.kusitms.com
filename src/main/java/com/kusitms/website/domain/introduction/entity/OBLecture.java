package com.kusitms.website.domain.introduction.entity;

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

    // 요청 순서대로 노출하기 위한 정렬값. 이 컬럼이 생기기 전 행은 null이라 id 순으로 정렬된다
    @Column(name = "display_order")
    private Integer displayOrder;

    @Builder
    public OBLecture(Introduction introduction, String imageUrl,
                         String name, String topic, Integer displayOrder) {
        this.introduction = introduction;
        introduction.getObLecture().add(this);
        this.imageUrl = imageUrl;
        this.name = name;
        this.topic = topic;
        this.displayOrder = displayOrder;
    }

    public void update(String name, String topic, String imageUrl, Integer displayOrder) {
        this.name = name;
        this.topic = topic;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
    }
}
