package com.kusitms.website.domain.introduction.dto.request;

// 소개 목록 항목 공통: id가 있으면 기존 항목 수정, 없으면 새 항목
public interface IntroListItemRequest {
    Long getId();
}
