package com.kusitms.website.domain.review;

import com.kusitms.website.global.common.BaseResponse;
import com.kusitms.website.domain.review.dto.response.ReviewDetailResponse;
import com.kusitms.website.domain.review.dto.response.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Review", description = "리뷰 API Document")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    @Operation(summary = "리뷰 리스트", description = "리뷰의 모든 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = ReviewResponse.class))),
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ReviewDetailResponse.class))),
    })
    public ResponseEntity<BaseResponse> getReviews() {
        return ResponseEntity.ok(new BaseResponse(reviewService.getReviews()));
    }
}
