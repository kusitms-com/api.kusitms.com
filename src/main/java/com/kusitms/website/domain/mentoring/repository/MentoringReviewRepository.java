package com.kusitms.website.domain.mentoring.repository;

import com.kusitms.website.domain.mentoring.entity.MentoringReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentoringReviewRepository extends JpaRepository<MentoringReview, Long> {

    Page<MentoringReview> findByMentorMentorIdOrderByCreatedAtDesc(Long mentorId, Pageable pageable);

    boolean existsByApplicationApplicationId(Long applicationId);
}
