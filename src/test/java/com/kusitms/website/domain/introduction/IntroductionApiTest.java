package com.kusitms.website.domain.introduction;

import com.kusitms.website.domain.file.service.S3Service;
import com.kusitms.website.domain.introduction.entity.BannerStatus;
import com.kusitms.website.domain.introduction.entity.Introduction;
import com.kusitms.website.domain.introduction.repository.IntroRepository;
import com.kusitms.website.global.util.S3Util;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class IntroductionApiTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    IntroRepository introRepository;

    @Autowired
    EntityManager entityManager;

    @MockBean
    S3Service s3Service;

    @Test
    @DisplayName("소개 정보가 없으면 공개 조회 API는 404를 반환한다")
    void getIntroductionWhenEmpty() throws Exception {
        mockMvc.perform(get("/api/introductions"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("학회 소개 정보가 존재하지 않습니다."));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("PUT 요청은 소개 정보를 최초 등록하고 이후에는 기존 행을 수정한다")
    void putIntroductionCreatesAndUpdates() throws Exception {
        MockMultipartFile bannerImage = new MockMultipartFile(
                "bannerImageFile",
                "banner.png",
                "image/png",
                "banner".getBytes()
        );
        given(s3Service.uploadFile(any(), eq("intro"))).willReturn("intro/banner.png");

        mockMvc.perform(validRequest(34, "34기 소개", 1400, 320, 80).file(bannerImage))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertThat(introRepository.count()).isEqualTo(1);

        mockMvc.perform(validRequest(35, "35기 소개", 1500, 350, 85))
                .andExpect(status().isOk());

        assertThat(introRepository.count()).isEqualTo(1);
        Introduction introduction = introRepository.findFirstByOrderByIntroductionIdAsc().orElseThrow();
        assertThat(introduction.getBannerCardinal()).isEqualTo(35);
        assertThat(introduction.getSlogan()).isEqualTo("35기 소개");
        assertThat(introduction.getBannerImageUrl()).isEqualTo("intro/banner.png");

        mockMvc.perform(get("/api/introductions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.banner_content").value("KUSITMS35기 리크루팅 종료"))
                .andExpect(jsonPath("$.data.slogan").value("35기 소개"))
                .andExpect(jsonPath("$.data.banner_image_url").value(S3Util.s3Url + "intro/banner.png"));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("소개 조회 API는 저장된 S3 키를 전체 URL로 반환하고, 이미지가 없으면 null을 반환한다")
    void getIntroductionReturnsFullImageUrls() throws Exception {
        given(s3Service.uploadFile(any(), eq("intro")))
                .willAnswer(invocation -> "intro/" + ((MultipartFile) invocation.getArgument(0)).getOriginalFilename());

        mockMvc.perform(validRequest(34, "34기 소개", 1400, 320, 80)
                        .file(new MockMultipartFile("planningImage", "planning.png", "image/png", "p".getBytes()))
                        .file(new MockMultipartFile("partnerLogoFiles", "verty.png", "image/png", "v".getBytes()))
                        .file(new MockMultipartFile("teams[0].imageFile", "team.jpg", "image/jpeg", "t".getBytes()))
                        .file(new MockMultipartFile("activities[0].imageFile1", "ot.webp", "image/webp", "o".getBytes()))
                        .param("teams[0].name", "학부학")
                        .param("teams[0].description", "학회 운영 총괄")
                        .param("activities[0].name", "OT")
                        .param("activities[0].description", "오리엔테이션"))
                .andExpect(status().isOk());

        // 실제 요청처럼 조회가 새 영속성 컨텍스트에서 DB를 읽도록 비운다
        entityManager.flush();
        entityManager.clear();

        // DB에는 키만 저장한다
        Introduction introduction = introRepository.findFirstByOrderByIntroductionIdAsc().orElseThrow();
        assertThat(introduction.getPlanningImageUrl()).isEqualTo("intro/planning.png");

        mockMvc.perform(get("/api/introductions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.planning_image_url").value(S3Util.s3Url + "intro/planning.png"))
                .andExpect(jsonPath("$.data.design_image_url").doesNotExist())
                .andExpect(jsonPath("$.data.partner_logo_urls[0]").value(S3Util.s3Url + "intro/verty.png"))
                .andExpect(jsonPath("$.data.teams[0].image_link").value(S3Util.s3Url + "intro/team.jpg"))
                .andExpect(jsonPath("$.data.activities[0].image_url1").value(S3Util.s3Url + "intro/ot.webp"))
                .andExpect(jsonPath("$.data.activities[0].image_url2").doesNotExist());

        mockMvc.perform(get("/admin/introductions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.planning_image_url").value(S3Util.s3Url + "intro/planning.png"))
                .andExpect(jsonPath("$.data.partner_logo_urls[0]").value(S3Util.s3Url + "intro/verty.png"))
                .andExpect(jsonPath("$.data.teams[0].image_link").value(S3Util.s3Url + "intro/team.jpg"));
    }

    @Test
    @DisplayName("이미 전체 URL로 저장된 이미지 값은 prefix를 다시 붙이지 않는다")
    void getIntroductionKeepsAbsoluteImageUrls() throws Exception {
        introRepository.save(Introduction.builder()
                .bannerCardinal(34L)
                .bannerStatus(BannerStatus.CLOSE)
                .slogan("34기 소개")
                .memberCount(1400L)
                .projectCount(320L)
                .universityCount(80L)
                .introYoutubeLink("https://www.youtube.com/watch?v=kusitms")
                .planningImageUrl("https://kusitms-bucket.s3.ap-northeast-2.amazonaws.com/intro/legacy.png")
                .designImageUrl("intro/design.png")
                .build());

        mockMvc.perform(get("/api/introductions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.planning_image_url")
                        .value("https://kusitms-bucket.s3.ap-northeast-2.amazonaws.com/intro/legacy.png"))
                .andExpect(jsonPath("$.data.design_image_url").value(S3Util.s3Url + "intro/design.png"));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("소개 등록 요청의 필수 값이 누락되면 400을 반환한다")
    void putIntroductionValidatesRequiredFields() throws Exception {
        mockMvc.perform(multipart(HttpMethod.PUT, "/admin/introductions")
                        .param("bannerCardinal", "34"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        assertThat(introRepository.count()).isZero();
    }

    private MockMultipartHttpServletRequestBuilder validRequest(
            int cardinal,
            String slogan,
            int memberCount,
            int projectCount,
            int universityCount
    ) {
        MockMultipartHttpServletRequestBuilder request = multipart(HttpMethod.PUT, "/admin/introductions");
        request.param("bannerCardinal", String.valueOf(cardinal))
                .param("bannerStatus", "CLOSE")
                .param("slogan", slogan)
                .param("memberCount", String.valueOf(memberCount))
                .param("projectCount", String.valueOf(projectCount))
                .param("universityCount", String.valueOf(universityCount))
                .param("introYoutubeLink", "https://www.youtube.com/watch?v=kusitms");
        return request;
    }
}
