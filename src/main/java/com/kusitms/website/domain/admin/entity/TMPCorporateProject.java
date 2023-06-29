package com.kusitms.website.domain.admin.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class TMPCorporateProject {
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

    @Builder
    public TMPCorporateProject(int cardinal, String name, String content,
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
}
