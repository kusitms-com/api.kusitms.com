package com.kusitms.website.domain.introduction;

import com.kusitms.website.domain.file.service.S3Service;
import com.kusitms.website.domain.introduction.entity.Introduction;
import com.kusitms.website.domain.introduction.repository.IntroRepository;
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
                .andExpect(jsonPath("$.data.banner_image_url").value("intro/banner.png"));
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
