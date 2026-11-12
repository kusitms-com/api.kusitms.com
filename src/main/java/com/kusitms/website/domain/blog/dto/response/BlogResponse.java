package com.kusitms.website.domain.blog.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kusitms.website.domain.blog.entity.BlogReview;
import com.kusitms.website.domain.blog.entity.BlogReviewActivity;
import com.kusitms.website.domain.blog.entity.Position;
import com.kusitms.website.global.util.S3Util;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

// 홈페이지 스토리 응답. blog_review로 옮기면서도 기존 필드명은 그대로 유지한다 (#92)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BlogResponse {
    @JsonProperty("blog_post_id")
    @Schema(description = "블로그 글 아이디")
    private Long id;
    @Schema(description = "블로그 글 제목")
    private String title;
    @Schema(description = "블로그 주소")
    private String address;
    @Schema(description = "블로그 이미지 주소")
    private String imageAddress;
    @Schema(description = "블로그 글 내용")
    private String content;
    @Schema(description = "활동명")
    private String categoryName;
    @Schema(description = "기수")
    private Integer cardinal;
    @Schema(description = "파트명")
    private String position;

    public static BlogResponse from(BlogReview blogReview) {
        return BlogResponse.builder()
                .id(blogReview.getBlogReviewId())
                .title(blogReview.getTitle())
                .address(blogReview.getBlogLink())
                .imageAddress(S3Util.toUrl(blogReview.getThumbnailUrl()))
                .content(blogReview.getPreviewText())
                .categoryName(BlogReviewActivity.descriptionOf(blogReview.getActivity()))
                .cardinal(blogReview.getCardinal())
                .position(Position.descriptionOf(blogReview.getPart()))
                .build();
    }
}
