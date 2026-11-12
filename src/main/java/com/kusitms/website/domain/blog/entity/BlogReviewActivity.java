package com.kusitms.website.domain.blog.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BlogReviewActivity {
    DOCUMENT_REVIEW("서류 후기"),
    INTERVIEW_REVIEW("면접 후기"),
    COMPANY_PROJECT("기업 프로젝트"),
    ACTIVITY_REVIEW("활동 후기"),
    MEETUP_PROJECT("밋업 프로젝트");

    private final String description;

    public static String descriptionOf(BlogReviewActivity activity) {
        return activity == null ? "기타" : activity.getDescription();
    }
}
