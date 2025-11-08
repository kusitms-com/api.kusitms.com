package com.kusitms.website.domain.blog.repository;

import com.kusitms.website.domain.blog.entity.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long>, BlogPostQueryRepository {
}
