package com.kusitms.website.domain.mentoring.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MentorMainResponse {

    @Schema(description = "활동 중인 멘토 카드 (최대 4개, 랜덤)")
    private List<MentorCardResponse> mentors;
}
