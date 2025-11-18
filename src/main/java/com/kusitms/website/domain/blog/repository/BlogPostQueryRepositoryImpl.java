package com.kusitms.website.domain.blog.repository;

import com.kusitms.website.domain.blog.dto.response.BlogResponse;
import com.kusitms.website.domain.blog.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
public class BlogPostQueryRepositoryImpl implements BlogPostQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final QBlogPost blogPost = QBlogPost.blogPost;
    private final QBlogAuthor blogAuthor = QBlogAuthor.blogAuthor;

    @Override
    public Page<BlogResponse> findByFiltersWithPaging(Integer generation, Position position, Category category, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        if (generation != null) {
            builder.and(blogAuthor.generation.eq(generation));
        }

        if (position != null) {
            builder.and(blogAuthor.position.eq(position));
        }

        if (category != null) {
            builder.and(blogPost.category.eq(category));
        }

        List<BlogResponse> results = queryFactory
                .select(Projections.constructor(
                        BlogResponse.class,
                        blogPost.id,
                        blogPost.title,
                        blogPost.address,
                        blogPost.imageAddress,
                        blogPost.content,
                        new CaseBuilder()
                                .when(blogPost.category.eq(Category.DOCUMENT)).then(Category.DOCUMENT.getDescription())
                                .when(blogPost.category.eq(Category.INTERVIEW)).then(Category.INTERVIEW.getDescription())
                                .when(blogPost.category.eq(Category.GIFT)).then(Category.GIFT.getDescription())
                                .when(blogPost.category.eq(Category.MEETUP)).then(Category.MEETUP.getDescription())
                                .when(blogPost.category.eq(Category.GROUP_TF)).then(Category.GROUP_TF.getDescription())
                                .otherwise("기타"),
                        blogAuthor.generation,
                        // Position → description
                        new CaseBuilder()
                                .when(blogAuthor.position.eq(Position.FRONTEND)).then(Position.FRONTEND.getDescription())
                                .when(blogAuthor.position.eq(Position.BACKEND)).then(Position.BACKEND.getDescription())
                                .when(blogAuthor.position.eq(Position.PLAN)).then(Position.PLAN.getDescription())
                                .when(blogAuthor.position.eq(Position.DESIGNER)).then(Position.DESIGNER.getDescription())
                                .otherwise("기타")
                ))
                .from(blogPost)
                .join(blogPost.blogAuthor, blogAuthor)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(blogPost.id.desc())
                .fetch();

        // ② total count 조회
        long total = queryFactory
                .select(blogPost.count())
                .from(blogPost)
                .join(blogPost.blogAuthor, blogAuthor)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }
}
