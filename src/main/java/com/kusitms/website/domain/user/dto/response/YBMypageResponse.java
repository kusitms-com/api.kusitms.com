package com.kusitms.website.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "YB 마이페이지 조회 응답")
public class YBMypageResponse {

    @Schema(description = "상단 프로필 정보")
    private AccountProfileResponse profile;

    @Schema(description = "대기 중 멘토링 목록")
    private List<MyMentoringCardResponse> pendingMentorings;

    @Schema(description = "진행 중 멘토링 목록")
    private List<MyMentoringCardResponse> activeMentorings;

    @Schema(description = "완료/거절 멘토링 목록")
    private List<MyMentoringCardResponse> completedMentorings;
}
