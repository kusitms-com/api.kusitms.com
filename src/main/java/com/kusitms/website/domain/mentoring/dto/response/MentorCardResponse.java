package com.kusitms.website.domain.mentoring.dto.response;

import com.kusitms.website.domain.mentoring.entity.Mentor;
import com.kusitms.website.domain.mentoring.entity.MentoringCategory;
import com.kusitms.website.domain.mentoring.entity.MentoringMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MentorCardResponse {

    @Schema(description = "멘토 ID")
    private Long mentorId;

    @Schema(description = "멘토링 타이틀")
    private String title;

    @Schema(description = "프로필 이미지 URL")
    private String profileImageUrl;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "기수")
    private Integer cardinal;

    @Schema(description = "직무 카테고리")
    private MentoringCategory category;

    @Schema(description = "경력")
    private String experience;

    @Schema(description = "멘토링 방식")
    private MentoringMethod method;

    @Schema(description = "시간당 가격")
    private Integer pricePerHour;

    @Schema(description = "뱃지 키워드 (조건부)")
    private String badgeKeyword;

    public static MentorCardResponse from(Mentor mentor, String badgeKeyword) {
        return MentorCardResponse.builder()
                .mentorId(mentor.getMentorId())
                .title(mentor.getTitle())
                .profileImageUrl(mentor.getProfileImageUrl())
                .name(mentor.getMember().getName())
                .cardinal(mentor.getMember().getCardinal())
                .category(mentor.getCategory())
                .experience(mentor.getExperience())
                .method(mentor.getMethod())
                .pricePerHour(mentor.getPricePerHour())
                .badgeKeyword(badgeKeyword)
                .build();
    }
}
