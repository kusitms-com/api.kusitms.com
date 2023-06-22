package com.kusitms.website.domain.project.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
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
}
