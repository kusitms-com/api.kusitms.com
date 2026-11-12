package com.kusitms.website.domain.blog.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 홈페이지 스토리 필터가 보내는 활동 값. 저장은 BlogReviewActivity 기준이라 조회 시 변환한다
@Getter
@RequiredArgsConstructor
public enum Category {
    DOCUMENT(BlogReviewActivity.DOCUMENT_REVIEW),
    INTERVIEW(BlogReviewActivity.INTERVIEW_REVIEW),
    GIFT(BlogReviewActivity.COMPANY_PROJECT),
    MEETUP(BlogReviewActivity.MEETUP_PROJECT),
    // 소모임/TF 후기는 데이터가 없어 활동 후기로 합쳤다 (#92)
    GROUP_TF(BlogReviewActivity.ACTIVITY_REVIEW);

    private final BlogReviewActivity activity;
}
