package com.kusitms.website.domain.mentoring.repository;

import com.kusitms.website.domain.mentoring.entity.MentoringReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface MentoringReviewRepository extends JpaRepository<MentoringReview, Long> {

    @EntityGraph(attributePaths = {"reviewer", "keywords", "keywords.keyword"})
    Page<MentoringReview> findByMentorMentorIdOrderByCreatedAtDesc(Long mentorId, Pageable pageable);

    boolean existsByApplicationApplicationId(Long applicationId);

    List<MentoringReview> findByApplicationApplicationIdIn(List<Long> applicationIds);
}
