package com.kusitms.website.domain.blog.repository;

import com.kusitms.website.domain.blog.dto.response.BlogResponse;
import com.kusitms.website.domain.blog.entity.BlogPost;
import com.kusitms.website.domain.blog.entity.Category;
import com.kusitms.website.domain.blog.entity.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface BlogPostQueryRepository {
    Page<BlogResponse> findByFiltersWithPaging(Integer generation, Position position, Category category, Pageable pageable);
}
