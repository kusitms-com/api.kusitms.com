package com.kusitms.website.domain.blog.repository;

import com.kusitms.website.domain.blog.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
public class BlogPostQueryRepositoryImpl implements BlogPostQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final QBlogPost blogPost = QBlogPost.blogPost;
    private final QBlogAuthor blogAuthor = QBlogAuthor.blogAuthor;

    @Override
    public List<BlogPost> findByFiltersWithPaging(Integer generation, Position position, Category category, Long lastId, int size) {

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

        if (lastId != null) {
            builder.and(blogPost.id.lt(lastId));
        }

        return queryFactory
                .selectFrom(blogPost)
                .join(blogPost.blogAuthor, blogAuthor).fetchJoin()
                .where(builder)
                .orderBy(blogPost.id.desc())
                .limit(size)
                .fetch();
    }
}
