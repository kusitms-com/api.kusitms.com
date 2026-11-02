package com.kusitms.website.domain.project.dto.response;

import com.kusitms.website.domain.project.entity.MeetupProject;
import com.kusitms.website.domain.project.entity.ProjectType;
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
}
