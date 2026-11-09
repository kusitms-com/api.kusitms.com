package com.kusitms.website.domain.project.dto.response;

import com.kusitms.website.domain.project.entity.MeetupProject;
import com.kusitms.website.domain.project.entity.ProjectType;
import com.kusitms.website.global.util.S3Util;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MeetupDetailResponseTest {

    @Test
    @DisplayName("meetupId 55의 기수 보정(30)은 공개 응답에만 적용되고, 어드민 응답은 실제 기수를 내려준다")
    void cardinalOverrideOnlyForPublic() {
        MeetupProject meetup = MeetupProject.builder()
                .cardinal(28)
                .name("OB 프로젝트")
                .type(ProjectType.WEB)
                .oneLineIntro("한 줄 소개")
                .logoUrl("meetup/logo.png")
                .posterUrl("meetup/poster.png")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 2, 1))
                .teamName("팀")
                .build();
        ReflectionTestUtils.setField(meetup, "meetupId", 55L);

        assertThat(new MeetupDetailResponse(meetup, false).getCardinal()).isEqualTo(30);
        assertThat(MeetupDetailResponse.forAdmin(meetup, false).getCardinal()).isEqualTo(28);
    }

    @Test
    @DisplayName("업로드 키(meetup/...)와 수동 입력 키(/meetup/...) 모두 prefix와 / 하나로 이어 URL을 만든다")
    void imageUrlsJoinPrefixWithSingleSlash() {
        String originalPrefix = S3Util.s3Url;
        S3Util.s3Url = "https://bucket.test";
        try {
            MeetupProject meetup = MeetupProject.builder()
                    .cardinal(34)
                    .name("밋업")
                    .type(ProjectType.WEB)
                    .oneLineIntro("한 줄 소개")
                    .logoUrl("meetup/logo.png")
                    .posterUrl("/meetup/poster/34th_web.png")
                    .startDate(LocalDate.of(2026, 1, 1))
                    .endDate(LocalDate.of(2026, 2, 1))
                    .teamName("팀")
                    .build();
            ReflectionTestUtils.setField(meetup, "meetupId", 1L);

            MeetupDetailResponse response = new MeetupDetailResponse(meetup, false);

            assertThat(response.getLogoUrl()).isEqualTo("https://bucket.test/meetup/logo.png");
            assertThat(response.getPosterUrl()).isEqualTo("https://bucket.test/meetup/poster/34th_web.png");
            assertThat(response.getWebThumbnailUrl()).isNull();
        } finally {
            S3Util.s3Url = originalPrefix;
        }
    }
}
