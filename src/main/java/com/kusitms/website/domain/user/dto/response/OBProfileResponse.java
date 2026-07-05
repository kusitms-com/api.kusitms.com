package com.kusitms.website.domain.user.dto.response;

import com.kusitms.website.domain.mentoring.entity.Mentor;
import com.kusitms.website.domain.mentoring.entity.MentoringCategory;
import com.kusitms.website.domain.mentoring.entity.MentoringMethod;
import com.kusitms.website.domain.user.Member;
import com.kusitms.website.domain.user.Part;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "OB 프로필 조회 응답")
public class OBProfileResponse {

    @Schema(description = "계정 프로필 이미지 URL")
    private String accountProfileImageUrl;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "기수")
    private Integer cardinal;

    @Schema(description = "파트")
    private Part part;

    @Schema(description = "멘토 카드용 프로필 이미지 URL")
    private String mentorProfileImageUrl;

    @Schema(description = "경력")
    private String experience;

    @Schema(description = "멘토링 제목")
    private String title;

    @Schema(description = "멘토링 소개")
    private String introduction;

    @Schema(description = "직무")
    private MentoringCategory category;

    @Schema(description = "멘토링 방식")
    private MentoringMethod method;

    @Schema(description = "멘토링 한타임 시간(분)")
    private Integer durationMinutes;

    @Schema(description = "멘토링 금액")
    private Integer pricePerHour;

    @Schema(description = "멘토링 신청 받기 토글 상태")
    private boolean acceptingRequests;

    @Schema(description = "실제 공개 여부")
    private boolean visible;

    public static OBProfileResponse from(Member member, Mentor mentor) {
        return OBProfileResponse.builder()
                .accountProfileImageUrl(member.getProfileImageUrl())
                .name(member.getName())
                .cardinal(member.getCardinal())
                .part(member.getPart())
                .mentorProfileImageUrl(mentor != null ? mentor.getProfileImageUrl() : null)
                .experience(mentor != null ? mentor.getExperience() : null)
                .title(mentor != null ? mentor.getTitle() : null)
                .introduction(mentor != null ? mentor.getIntroduction() : null)
                .category(mentor != null ? mentor.getCategory() : null)
                .method(mentor != null ? mentor.getMethod() : null)
                .durationMinutes(mentor != null ? mentor.getDurationMinutes() : null)
                .pricePerHour(mentor != null ? mentor.getPricePerHour() : null)
                .acceptingRequests(mentor != null && mentor.isAcceptingRequests())
                .visible(mentor != null && mentor.isActive())
                .build();
    }
}
