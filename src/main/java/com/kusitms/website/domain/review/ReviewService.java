package com.kusitms.website.domain.review;

import com.kusitms.website.domain.project.entity.Team;
import com.kusitms.website.domain.review.entity.Review;
import com.kusitms.website.domain.review.dto.response.ReviewDetailResponse;
import com.kusitms.website.domain.review.dto.response.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewResponse getAllReviews() {
        List<Review> findReviews = reviewRepository.findAllByOrderByReviewIdDesc();
        return buildReviewResponse(findReviews);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewsByTeam(Team team) {
        List<Review> findReviews = reviewRepository.findByTeamOrderByReviewIdDesc(team);
        return buildReviewResponse(findReviews);
    }

    private ReviewResponse buildReviewResponse(List<Review> findReviews) {
        List<ReviewDetailResponse> reviewDetailResponses = findReviews.stream()
                .map(r -> new ReviewDetailResponse(r.getReviewId(), r.getName(), r.getTeam(), r.getReview()))
                .collect(Collectors.toList());

        return ReviewResponse.builder()
                .reviewCount(findReviews.size())
                .reviewList(reviewDetailResponses)
                .build();
    }
}