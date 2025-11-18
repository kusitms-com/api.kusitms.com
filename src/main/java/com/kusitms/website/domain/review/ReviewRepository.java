package com.kusitms.website.domain.review;

import com.kusitms.website.domain.project.entity.Team;
import com.kusitms.website.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByOrderByReviewIdDesc();
    List<Review> findByTeamOrderByReviewIdDesc(Team team);

    List<Review> findByCardinalOrderByReviewIdDesc(Integer cardinal);

    List<Review> findByTeamAndCardinalOrderByReviewIdDesc(Team team, Integer cardinal);
}
