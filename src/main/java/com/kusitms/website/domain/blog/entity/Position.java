package com.kusitms.website.domain.blog.entity;

import com.kusitms.website.domain.project.entity.Team;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

// 홈페이지 스토리 필터가 보내는 파트 값. 저장은 Team 기준이라 조회 시 변환한다
@Getter
@RequiredArgsConstructor
public enum Position {
    FRONTEND(Team.FRONTEND, "프론트엔드"),
    BACKEND(Team.BACKEND, "백엔드"),
    PLAN(Team.PLANNER, "기획"),
    DESIGNER(Team.DESIGNER, "디자이너");

    private final Team team;
    private final String description;

    public static String descriptionOf(Team team) {
        return Arrays.stream(values())
                .filter(position -> position.team == team)
                .map(Position::getDescription)
                .findFirst()
                .orElse("기타");
    }
}
