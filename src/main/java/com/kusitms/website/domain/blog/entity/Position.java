package com.kusitms.website.domain.blog.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Position {
    FRONTEND("프론트엔드"),
    BACKEND("백엔드"),
    PLAN("기획"),
    DESIGNER("디자이너");

    private final String description;

}
