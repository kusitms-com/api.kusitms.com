package com.kusitms.website.domain.blog.service;

import com.kusitms.website.domain.blog.dto.response.BlogResponse;
import com.kusitms.website.domain.blog.entity.BlogPost;
import com.kusitms.website.domain.blog.entity.Category;
import com.kusitms.website.domain.blog.entity.Position;
import com.kusitms.website.domain.blog.repository.BlogPostQueryRepository;
import com.kusitms.website.domain.blog.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogPostRepository blogPostRepository;

    public List<BlogResponse> getFilteredPostsWithPaging(
            Integer generation,
            Position position,
            Category category,
            Long lastId,
            int size
    ) {
        return blogPostRepository.findByFiltersWithPaging(generation, position, category, lastId, size)
                .stream()
                .map(BlogResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
