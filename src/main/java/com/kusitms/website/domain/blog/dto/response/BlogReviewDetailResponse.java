package com.kusitms.website.domain.blog.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kusitms.website.domain.admin.entity.TMPBlogReview;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import static com.kusitms.website.global.util.S3Util.s3Url;

@Getter
@Schema
public class BlogReviewDetailResponse {
    @JsonProperty("blog_review_id")
    @Schema(description = "블로그 후기 아이디")
    private Long blogReviewId;

    @Schema(description = "기수")
    private Integer cardinal;

    @Schema(description = "파트")
    private String part;

    @Schema(description = "활동")
    private String activity;

    @Schema(description = "썸네일 URL")
    private String thumbnailUrl;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "미리보기 텍스트")
    private String previewText;

    public BlogReviewDetailResponse(TMPBlogReview blogReview) {
        this.blogReviewId = blogReview.getBlogReviewId();
        this.cardinal = blogReview.getCardinal();
        this.part = blogReview.getPart() == null ? null : blogReview.getPart().name();
        this.activity = blogReview.getActivity() == null ? null : blogReview.getActivity().name();
        this.thumbnailUrl = blogReview.getThumbnailUrl() == null ? null : s3Url + blogReview.getThumbnailUrl();
        this.title = blogReview.getTitle();
        this.previewText = blogReview.getPreviewText();
    }
}
