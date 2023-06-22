package com.kusitms.website.domain.introduction;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Contact {
    @Id
    @Column(name = "contact_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contactId;

    // 정관
    @Column(name = "article_link")
    private String articleLink;

    // 컨택
    private String email;
    private String instagram;
    private String youtube;
    private String cafe;
    private String github;

    @Builder Contact(String articleLink,
                     String email, String instagram, String youtube,
                     String cafe, String github) {
        this.articleLink = articleLink;
        this.email = email;
        this.instagram = instagram;
        this.youtube = youtube;
        this.cafe = cafe;
        this.github = github;
    }
}
