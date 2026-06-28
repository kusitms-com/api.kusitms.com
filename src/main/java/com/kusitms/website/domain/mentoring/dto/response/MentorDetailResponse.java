package com.kusitms.website.domain.mentoring.dto.response;

import com.kusitms.website.domain.mentoring.entity.Mentor;
import com.kusitms.website.domain.mentoring.entity.MentoringCategory;
import com.kusitms.website.domain.mentoring.entity.MentoringMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MentorDetailResponse {

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

    @Schema(description = "멘토링 소개글")
    private String introduction;

    @Schema(description = "가용 슬롯 목록")
    private List<MentoringSlotResponse> slots;

    @Schema(description = "키워드 칩 목록 (3회 이상)")
    private List<KeywordChipResponse> keywordChips;

    @Schema(description = "후기 목록")
    private MentoringReviewListResponse reviews;

    @Schema(description = "본인 멘토링 여부")
    private boolean isOwnMentoring;

    @Schema(description = "기존 PENDING/ACTIVE 신청 존재 여부")
    private boolean hasExistingApplication;

    public static MentorDetailResponse from(Mentor mentor,
                                            List<MentoringSlotResponse> slots,
                                            List<KeywordChipResponse> keywordChips,
                                            MentoringReviewListResponse reviews,
                                            boolean isOwnMentoring,
                                            boolean hasExistingApplication) {
        return MentorDetailResponse.builder()
                .mentorId(mentor.getMentorId())
                .title(mentor.getTitle())
                .profileImageUrl(mentor.getProfileImageUrl())
                .name(mentor.getMember().getName())
                .cardinal(mentor.getMember().getCardinal())
                .category(mentor.getCategory())
                .experience(mentor.getExperience())
                .method(mentor.getMethod())
                .pricePerHour(mentor.getPricePerHour())
                .introduction(mentor.getIntroduction())
                .slots(slots)
                .keywordChips(keywordChips)
                .reviews(reviews)
                .isOwnMentoring(isOwnMentoring)
                .hasExistingApplication(hasExistingApplication)
                .build();
    }
}
