package com.kusitms.website.domain.blog.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema
public class BlogReviewResponse {
    @JsonProperty("blog_review_count")
    @Schema(description = "블로그 후기 개수")
    private int blogReviewCount;

    @JsonProperty("blog_review_list")
    @Schema(description = "블로그 후기 리스트")
    private List<BlogReviewDetailResponse> blogReviewList;
}
