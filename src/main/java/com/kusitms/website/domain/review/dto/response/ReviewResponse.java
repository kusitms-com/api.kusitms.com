package com.kusitms.website.domain.review.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Schema
@Builder
public class ReviewResponse {
    @JsonProperty("review_count")
    @Schema(description = "리뷰 개수")
    private int reviewCount;

    @JsonProperty("review_list")
    @Schema(description = "리뷰 리스트")
    private List<ReviewDetailResponse> reviewList;
}
