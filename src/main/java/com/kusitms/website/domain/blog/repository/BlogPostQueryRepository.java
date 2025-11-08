package com.kusitms.website.domain.blog.repository;

import com.kusitms.website.domain.blog.entity.BlogPost;
import com.kusitms.website.domain.blog.entity.Category;
import com.kusitms.website.domain.blog.entity.Position;

import java.util.List;

public interface BlogPostQueryRepository {
    List<BlogPost> findByFiltersWithPaging(Integer generation, Position position, Category category, Long lastId, int size);
}
