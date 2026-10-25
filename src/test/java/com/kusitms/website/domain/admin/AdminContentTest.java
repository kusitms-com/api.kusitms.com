package com.kusitms.website.domain.admin;

import com.kusitms.website.domain.file.S3Service;
import com.kusitms.website.domain.project.CorporateRepository;
import com.kusitms.website.domain.project.MeetupRepository;
import com.kusitms.website.domain.project.MeetupTeamRepository;
import com.kusitms.website.domain.project.TagRepository;
import com.kusitms.website.domain.project.entity.CorporateProject;
import com.kusitms.website.domain.project.entity.MeetupProject;
import com.kusitms.website.domain.review.ReviewRepository;
import com.kusitms.website.domain.user.Member;
import com.kusitms.website.domain.user.MemberAuthority;
import com.kusitms.website.domain.user.MemberRepository;
import com.kusitms.website.domain.user.MemberRole;
import com.kusitms.website.domain.user.MemberStatus;
import com.kusitms.website.global.auth.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminContentTest {

    @Autowired MockMvc mockMvc;
    @Autowired EntityManager em;
    @Autowired MemberRepository memberRepository;
    @Autowired JwtTokenProvider jwtTokenProvider;
    @Autowired CorporateRepository corporateRepository;
    @Autowired MeetupRepository meetupRepository;
    @Autowired MeetupTeamRepository meetupTeamRepository;
    @Autowired TagRepository tagRepository;
    @Autowired ReviewRepository reviewRepository;

    @MockBean S3Service s3Service;

    Member manager;

    @BeforeEach
    void setUp() {
        // 업로드 결과를 "디렉토리/원본파일명"으로 고정해 어떤 파일이 저장됐는지 검증한다
        given(s3Service.uploadFile(any(MultipartFile.class), anyString()))
                .willAnswer(inv -> inv.getArgument(1) + "/" + ((MultipartFile) inv.getArgument(0)).getOriginalFilename());

        Member member = Member.builder()
                .id("manager")
                .password("password")
                .name("manager")
                .cardinal(34)
                .status(MemberStatus.APPROVED)
                .role(MemberRole.YB)
                .build();
        member.updateAuthority(MemberAuthority.MANAGER);
        manager = memberRepository.saveAndFlush(member);
    }

    @Test
    @DisplayName("기업 프로젝트: 어드민 등록 내용이 공개 API에 태그와 함께 노출되고, 파일 없이 수정하면 기존 이미지를 유지한다")
    void corporateProject() throws Exception {
        mockMvc.perform(auth(multipart("/admin/projects/corporate")
                        .file(image("logoFile", "logo.png"))
                        .file(image("bannerFile", "banner.png"))
                        .param("cardinal", "34")
                        .param("name", "큐시즘")
                        .param("content", "어드민 페이지 만들기")
                        .param("tag", "#웹", "앱", "웹")))
                .andExpect(status().isOk());
        flushAndClear();

        CorporateProject saved = corporateRepository.findAllByCardinal(34).get(0);
        assertThat(saved.getCategory()).isEqualTo("웹#앱");

        mockMvc.perform(get("/api/projects/corporate").param("cardinal", "34"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.corporateList[0].name").value("큐시즘"))
                .andExpect(jsonPath("$.data.corporateList[0].tags", containsInAnyOrder("#웹", "#앱")));

        // 파일 없이 수정: 이미지 유지, 태그 교체, 기존 태그 재사용
        mockMvc.perform(auth(multipart("/admin/projects/corporate").with(asPut())
                        .param("corporateId", saved.getCorporateId().toString())
                        .param("cardinal", "34")
                        .param("name", "큐시즘2")
                        .param("content", "수정")
                        .param("tag", "앱", "AI")))
                .andExpect(status().isOk());
        flushAndClear();

        CorporateProject updated = corporateRepository.findById(saved.getCorporateId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("큐시즘2");
        assertThat(updated.getLogoUrl()).isEqualTo("corporate/logo.png");
        assertThat(updated.getBannerUrl()).isEqualTo("corporate/banner.png");
        assertThat(updated.getTags()).extracting("name").containsExactlyInAnyOrder("앱", "AI");
        assertThat(tagRepository.findAll()).extracting("name").containsExactlyInAnyOrder("웹", "앱", "AI");

        mockMvc.perform(auth(delete("/admin/projects/corporate/" + saved.getCorporateId())))
                .andExpect(status().isOk());
        flushAndClear();
        assertThat(corporateRepository.findById(saved.getCorporateId())).isEmpty();
    }

    @Test
    @DisplayName("밋업 프로젝트: 어드민 등록 내용이 공개 API에 노출되고, 수정 시 팀원·태그는 교체되고 이미지는 유지된다")
    void meetupProject() throws Exception {
        mockMvc.perform(auth(meetupRequest(multipart("/admin/projects/meetup")
                        .file(image("logoFile", "logo.png"))
                        .file(image("posterFile", "poster.png"))
                        .file(image("webThumbnailFile", "thumb.png")))
                        .param("planner", "김기획")
                        .param("backend", "이백엔드", "박백엔드")
                        .param("tag", "웹")))
                .andExpect(status().isOk());
        flushAndClear();

        MeetupProject saved = meetupRepository.findAllByCardinal(34).get(0);
        assertThat(saved.getWebThumbnailUrl()).isEqualTo("meetup/thumb.png");
        assertThat(meetupTeamRepository.count()).isEqualTo(3);

        mockMvc.perform(get("/api/projects/meetup/" + saved.getMeetupId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("피크랩"))
                .andExpect(jsonPath("$.data.web_thumbnail_url", endsWith("meetup/thumb.png")))
                .andExpect(jsonPath("$.data.team.backend", containsInAnyOrder("이백엔드", "박백엔드")));

        // 파일 없이 수정: 이미지 유지, 팀원·태그 교체
        mockMvc.perform(auth(meetupRequest(multipart("/admin/projects/meetup")).with(asPut())
                        .param("meetupId", saved.getMeetupId().toString())
                        .param("designer", "최디자인")
                        .param("tag", "앱")))
                .andExpect(status().isOk());
        flushAndClear();

        MeetupProject updated = meetupRepository.findById(saved.getMeetupId()).orElseThrow();
        assertThat(updated.getLogoUrl()).isEqualTo("meetup/logo.png");
        assertThat(updated.getPosterUrl()).isEqualTo("meetup/poster.png");
        assertThat(updated.getWebThumbnailUrl()).isEqualTo("meetup/thumb.png");
        assertThat(updated.getTeam()).extracting("name").containsExactly("최디자인");
        assertThat(updated.getTags()).extracting("name").containsExactly("앱");
        assertThat(meetupTeamRepository.count()).isEqualTo(1);

        mockMvc.perform(auth(get("/admin/meetup/" + saved.getMeetupId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tags[0]").value("#앱"));

        mockMvc.perform(auth(delete("/admin/projects/meetup/" + saved.getMeetupId())))
                .andExpect(status().isOk());
        flushAndClear();
        assertThat(meetupRepository.findById(saved.getMeetupId())).isEmpty();
        assertThat(meetupTeamRepository.count()).isZero();
    }

    @Test
    @DisplayName("선택 입력이 없으면 웹 썸네일은 null, 기업 프로젝트 분류는 빈 목록으로 응답한다")
    void optionalFieldsAreEmpty() throws Exception {
        mockMvc.perform(auth(meetupRequest(multipart("/admin/projects/meetup")
                        .file(image("logoFile", "logo.png"))
                        .file(image("posterFile", "poster.png")))))
                .andExpect(status().isOk());
        mockMvc.perform(auth(multipart("/admin/projects/corporate")
                        .file(image("logoFile", "logo.png"))
                        .file(image("bannerFile", "banner.png"))
                        .param("cardinal", "34")
                        .param("name", "큐시즘")
                        .param("content", "태그 없음")))
                .andExpect(status().isOk());
        flushAndClear();

        mockMvc.perform(get("/api/projects/meetup").param("cardinal", "34"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.meetup_list[0].web_thumbnail_url").value(nullValue()));
        mockMvc.perform(get("/api/projects/corporate").param("cardinal", "34"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.corporateList[0].category", empty()));
    }

    @Test
    @DisplayName("학회원 후기: 어드민 등록/수정/삭제가 공개 API에 반영된다")
    void review() throws Exception {
        mockMvc.perform(auth(post("/admin/review")).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"김큐시\",\"cardinal\":34,\"team\":\"BACKEND\",\"review\":\"좋아요\"}"))
                .andExpect(status().isOk());
        flushAndClear();

        Long reviewId = reviewRepository.findAllByOrderByReviewIdDesc().get(0).getReviewId();
        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.review_list[0].review").value("좋아요"));

        mockMvc.perform(auth(put("/admin/review")).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reviewId\":" + reviewId + ",\"name\":\"김큐시\",\"cardinal\":34,\"team\":\"BACKEND\",\"review\":\"최고예요\"}"))
                .andExpect(status().isOk());
        flushAndClear();
        assertThat(reviewRepository.findById(reviewId).orElseThrow().getReview()).isEqualTo("최고예요");

        mockMvc.perform(auth(delete("/admin/review/" + reviewId))).andExpect(status().isOk());
        flushAndClear();
        assertThat(reviewRepository.findById(reviewId)).isEmpty();
    }

    private MockMultipartHttpServletRequestBuilder meetupRequest(MockMultipartHttpServletRequestBuilder request) {
        request.param("cardinal", "34")
                .param("name", "피크랩")
                .param("intro", "소개")
                .param("type", "WEB")
                .param("oneLineIntro", "한 줄 소개")
                .param("startDate", "2026-09-01")
                .param("endDate", "2026-12-20")
                .param("teamName", "오텐션");
        return request;
    }

    private MockMultipartFile image(String name, String filename) {
        return new MockMultipartFile(name, filename, "image/png", new byte[]{1});
    }

    private static org.springframework.test.web.servlet.request.RequestPostProcessor asPut() {
        return request -> {
            request.setMethod("PUT");
            return request;
        };
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    private MockHttpServletRequestBuilder auth(MockHttpServletRequestBuilder request) {
        return request.header("Authorization", "Bearer " + jwtTokenProvider.makeJwtToken(manager.getUserId()));
    }
}
