package com.kusitms.website.domain.blog.service;

import com.kusitms.website.domain.blog.dto.response.BlogResponse;
import com.kusitms.website.domain.blog.entity.Category;
import com.kusitms.website.domain.blog.entity.Position;
import com.kusitms.website.domain.blog.repository.BlogReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogReviewRepository blogReviewRepository;

    // 어드민에서 등록한 블로그 후기(blog_review)를 홈페이지 필터 값으로 조회한다
    @Transactional(readOnly = true)
    public Page<BlogResponse> getFilteredPostsWithPaging(
            Integer generation,
            Position position,
            Category category,
            Pageable pageable
    ) {
        return blogReviewRepository.findByFiltersWithPaging(
                generation,
                position == null ? null : position.getTeam(),
                category == null ? null : category.getActivity(),
                pageable
        ).map(BlogResponse::from);
    }
}
