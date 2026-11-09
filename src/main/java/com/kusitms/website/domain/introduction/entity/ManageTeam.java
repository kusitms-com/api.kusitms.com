package com.kusitms.website.domain.introduction.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ManageTeam {
    @Id
    @Column(name = "team_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "introduction_id")
    private Introduction introduction;

    private String name;

    @Column(name="image_url")
    private String imageUrl;
    private String description;

    // 요청 순서대로 노출하기 위한 정렬값. 이 컬럼이 생기기 전 행은 null이라 id 순으로 정렬된다
    @Column(name = "display_order")
    private Integer displayOrder;

    @Builder
    public ManageTeam(Introduction introduction, String name,
                      String imageUrl, String description, Integer displayOrder) {
        this.introduction = introduction;
        introduction.getManageTeam().add(this);
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.displayOrder = displayOrder;
    }

    public void update(String name, String description, String imageUrl, Integer displayOrder) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
    }
}
