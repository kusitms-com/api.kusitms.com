package com.kusitms.website.domain.project.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CorporateProject {
    @Id
    @Column(name = "corporate_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long corporateId;

    private int cardinal;

    private String name;

    @Column(length = 40)
    private String content;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "banner_url")
    private String bannerUrl;

    private String category;

    @ManyToMany
    @JoinTable(
            name = "corporate_project_tags",
            joinColumns = @JoinColumn(name = "corporate_project_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @Builder
    public CorporateProject(int cardinal, String name, String content,
                            String logoUrl, String bannerUrl, String category) {
        this.cardinal = cardinal;
        this.name = name;
        this.content = content;
        this.logoUrl = logoUrl;
        this.bannerUrl = bannerUrl;
        this.category = category;
    }

    public void update(int cardinal, String name, String content,
                       String logoUrl, String bannerUrl, String category) {
        this.cardinal = cardinal;
        this.name = name;
        this.content = content;
        this.logoUrl = logoUrl;
        this.bannerUrl = bannerUrl;
        this.category = category;
    }

    public void updateTags(Set<Tag> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
    }
}
