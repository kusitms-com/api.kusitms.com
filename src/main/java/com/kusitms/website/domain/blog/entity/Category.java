package com.kusitms.website.domain.blog.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    DOCUMENT("서류 후기"),
    INTERVIEW("면접 후기"),
    GIFT("기프 후기"),
    MEETUP("밋업 후기"),
    GROUP_TF("소모임/TF 후기");

    private final String description;
}
