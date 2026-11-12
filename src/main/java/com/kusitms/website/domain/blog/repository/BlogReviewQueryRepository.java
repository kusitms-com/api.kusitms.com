package com.kusitms.website.domain.blog.repository;

import com.kusitms.website.domain.blog.entity.BlogReview;
import com.kusitms.website.domain.blog.entity.BlogReviewActivity;
import com.kusitms.website.domain.project.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlogReviewQueryRepository {
    Page<BlogReview> findByFiltersWithPaging(Integer cardinal, Team part, BlogReviewActivity activity, Pageable pageable);
}
