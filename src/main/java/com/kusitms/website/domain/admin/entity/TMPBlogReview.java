package com.kusitms.website.domain.admin.entity;

import com.kusitms.website.domain.project.entity.Team;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TMPBlogReview {
    @Id
    @Column(name = "blog_review_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blogReviewId;

    private Integer cardinal;

    @Enumerated(EnumType.STRING)
    private Team part;

    @Enumerated(EnumType.STRING)
    private BlogReviewActivity activity;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "blog_link", length = 2048)
    private String blogLink;

    private String title;

    @Column(length = 500)
    private String previewText;

    @Builder
    public TMPBlogReview(Integer cardinal, Team part, BlogReviewActivity activity,
                         String thumbnailUrl, String blogLink, String title, String previewText) {
        this.cardinal = cardinal;
        this.part = part;
        this.activity = activity;
        this.thumbnailUrl = thumbnailUrl;
        this.blogLink = blogLink;
        this.title = title;
        this.previewText = previewText;
    }

    public void update(Integer cardinal, Team part, BlogReviewActivity activity,
                       String thumbnailUrl, String blogLink, String title, String previewText) {
        this.cardinal = cardinal;
        this.part = part;
        this.activity = activity;
        this.thumbnailUrl = thumbnailUrl;
        this.blogLink = blogLink;
        this.title = title;
        this.previewText = previewText;
    }
}
