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

    @Builder
    public ManageTeam(Introduction introduction, String name,
                      String imageUrl, String description) {
        this.introduction = introduction;
        introduction.getManageTeam().add(this);
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
    }
}
