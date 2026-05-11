package com.kusitms.website.domain.admin.repository;

import com.kusitms.website.domain.admin.entity.TMPBlogReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TMPBlogReviewRepository extends JpaRepository<TMPBlogReview, Long> {
    List<TMPBlogReview> findAllByOrderByBlogReviewIdDesc();
}
