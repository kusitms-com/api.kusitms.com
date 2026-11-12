package com.kusitms.website.domain.blog.repository;

import com.kusitms.website.domain.blog.entity.BlogReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogReviewRepository extends JpaRepository<BlogReview, Long>, BlogReviewQueryRepository {
    List<BlogReview> findAllByOrderByBlogReviewIdDesc();
}
