package com.kusitms.website.domain.blog.repository;

import com.kusitms.website.domain.blog.entity.BlogReview;
import com.kusitms.website.domain.blog.entity.BlogReviewActivity;
import com.kusitms.website.domain.blog.entity.QBlogReview;
import com.kusitms.website.domain.project.entity.Team;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class BlogReviewQueryRepositoryImpl implements BlogReviewQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final QBlogReview blogReview = QBlogReview.blogReview;

    @Override
    public Page<BlogReview> findByFiltersWithPaging(Integer cardinal, Team part, BlogReviewActivity activity, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (cardinal != null) {
            builder.and(blogReview.cardinal.eq(cardinal));
        }

        if (part != null) {
            builder.and(blogReview.part.eq(part));
        }

        if (activity != null) {
            builder.and(blogReview.activity.eq(activity));
        }

        List<BlogReview> results = queryFactory
                .selectFrom(blogReview)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(blogReview.blogReviewId.desc())
                .fetch();

        // 첫 페이지에 결과가 다 들어오면 count 쿼리를 생략한다
        JPAQuery<Long> countQuery = queryFactory
                .select(blogReview.count())
                .from(blogReview)
                .where(builder);

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }
}
