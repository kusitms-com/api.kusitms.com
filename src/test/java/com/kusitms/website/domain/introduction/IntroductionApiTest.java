package com.kusitms.website.domain.introduction;

import com.kusitms.website.domain.file.service.S3Service;
import com.kusitms.website.domain.introduction.entity.BannerStatus;
import com.kusitms.website.domain.introduction.entity.Introduction;
import com.kusitms.website.domain.introduction.repository.IntroRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

    // CI는 시크릿으로 만든 application-test.properties로 prefix를 덮어쓰므로 주입된 값을 쓴다
    @Value("${aws.s3.prefix}")
    String s3Prefix;

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
        given(s3Service.uploadFile(any(), eq("intro"))).willReturn("intro/planning.png");

        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .file(new MockMultipartFile("planningImage", "planning.png", "image/png", "p".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertThat(introRepository.count()).isEqualTo(1);

        // 제거된 slogan 을 보내도 무시하고 성공한다 (하위 호환)
        mockMvc.perform(validRequest(35, 1500, 350, 85).param("slogan", "예전 슬로건"))
                .andExpect(status().isOk());

        assertThat(introRepository.count()).isEqualTo(1);
        Introduction introduction = introRepository.findFirstByOrderByIntroductionIdAsc().orElseThrow();
        assertThat(introduction.getBannerCardinal()).isEqualTo(35);
        assertThat(introduction.getPlanningImageUrl()).isEqualTo("intro/planning.png");

        mockMvc.perform(get("/api/introductions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.banner_content").value("KUSITMS35기 리크루팅 종료"))
                .andExpect(jsonPath("$.data.slogan").doesNotExist())
                .andExpect(jsonPath("$.data.banner_image_url").doesNotExist());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("소개 조회 API는 저장된 S3 키를 전체 URL로 반환하고, 이미지가 없으면 null을 반환한다")
    void getIntroductionReturnsFullImageUrls() throws Exception {
        givenUploadReturnsFileName();

        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .file(new MockMultipartFile("planningImage", "planning.png", "image/png", "p".getBytes()))
                        .file(new MockMultipartFile("partnerLogos[0].imageFile", "verty.png", "image/png", "v".getBytes()))
                        .file(new MockMultipartFile("teams[0].imageFile", "team.jpg", "image/jpeg", "t".getBytes()))
                        .file(new MockMultipartFile("activities[0].imageFile1", "ot.webp", "image/webp", "o".getBytes()))
                        .param("teams[0].name", "학부학")
                        .param("teams[0].description", "학회 운영 총괄")
                        .param("activities[0].name", "OT")
                        .param("activities[0].description", "오리엔테이션"))
                .andExpect(status().isOk());

        clearPersistenceContext();

        // DB에는 키만 저장한다
        Introduction introduction = introRepository.findFirstByOrderByIntroductionIdAsc().orElseThrow();
        assertThat(introduction.getPlanningImageUrl()).isEqualTo("intro/planning.png");

        mockMvc.perform(get("/api/introductions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.planning_image_url").value(url("intro/planning.png")))
                .andExpect(jsonPath("$.data.design_image_url").doesNotExist())
                .andExpect(jsonPath("$.data.partner_logo_urls[0]").value(url("intro/verty.png")))
                .andExpect(jsonPath("$.data.teams[0].image_link").value(url("intro/team.jpg")))
                .andExpect(jsonPath("$.data.activities[0].image_url1").value(url("intro/ot.webp")))
                .andExpect(jsonPath("$.data.activities[0].image_url2").doesNotExist());

        mockMvc.perform(get("/admin/introductions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.planning_image_url").value(url("intro/planning.png")))
                .andExpect(jsonPath("$.data.partner_logo_urls[0]").value(url("intro/verty.png")))
                .andExpect(jsonPath("$.data.teams[0].image_link").value(url("intro/team.jpg")));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("목록 수정 시 새 파일이 없으면 기존 이미지 URL로 이미지를 유지하고, 요청 순서대로 교체한다")
    void putIntroductionKeepsExistingListImages() throws Exception {
        givenUploadReturnsFileName();

        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .file(new MockMultipartFile("teams[0].imageFile", "team0.jpg", "image/jpeg", "t".getBytes()))
                        .file(new MockMultipartFile("teams[1].imageFile", "team1.jpg", "image/jpeg", "t".getBytes()))
                        .file(new MockMultipartFile("partnerLogos[0].imageFile", "lg.png", "image/png", "l".getBytes()))
                        .file(new MockMultipartFile("partnerLogos[1].imageFile", "verty.png", "image/png", "v".getBytes()))
                        .param("teams[0].name", "학부학")
                        .param("teams[0].description", "기존 설명")
                        .param("teams[1].name", "경영총괄팀")
                        .param("teams[1].description", "삭제될 팀"))
                .andExpect(status().isOk());
        clearPersistenceContext();

        // 학부학은 설명만 수정(이미지 유지), 경영총괄팀은 삭제, 로고는 순서 변경 + 새 로고 추가
        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .file(new MockMultipartFile("partnerLogos[2].imageFile", "gabia.png", "image/png", "g".getBytes()))
                        .param("teams[0].name", "학부학")
                        .param("teams[0].description", "수정된 설명")
                        .param("teams[0].imageUrl", url("intro/team0.jpg"))
                        .param("partnerLogos[0].imageUrl", url("intro/verty.png"))
                        .param("partnerLogos[1].imageUrl", url("intro/lg.png")))
                .andExpect(status().isOk());
        clearPersistenceContext();

        mockMvc.perform(get("/admin/introductions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.teams.length()").value(1))
                .andExpect(jsonPath("$.data.teams[0].description").value("수정된 설명"))
                .andExpect(jsonPath("$.data.teams[0].image_link").value(url("intro/team0.jpg")))
                .andExpect(jsonPath("$.data.partner_logo_urls.length()").value(3))
                .andExpect(jsonPath("$.data.partner_logo_urls[0]").value(url("intro/verty.png")))
                .andExpect(jsonPath("$.data.partner_logo_urls[1]").value(url("intro/lg.png")))
                .andExpect(jsonPath("$.data.partner_logo_urls[2]").value(url("intro/gabia.png")));

        // DB에는 URL이 아닌 키로 저장한다
        Introduction introduction = introRepository.findFirstByOrderByIntroductionIdAsc().orElseThrow();
        assertThat(introduction.getManageTeam()).extracting("imageUrl").containsExactly("intro/team0.jpg");
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("우리 버킷이 아닌 기존 이미지 URL은 400을 반환한다")
    void putIntroductionRejectsExternalImageUrl() throws Exception {
        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .file(new MockMultipartFile("planningImage", "planning.png", "image/png", "p".getBytes()))
                        .param("teams[0].name", "학부학")
                        .param("teams[0].description", "설명")
                        .param("teams[0].imageUrl", "https://evil.example.com/x.png"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        // 검증이 업로드보다 먼저라 S3에 파일이 남지 않는다
        verify(s3Service, never()).uploadFile(any(), any());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("우리 버킷이라도 소개 이미지 폴더가 아닌 URL은 400을 반환한다")
    void putIntroductionRejectsNonIntroImageUrl() throws Exception {
        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .param("sponsors[0].imageUrl", url("profile/member.png")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("경로에 ..가 들어간 기존 이미지 URL은 400을 반환한다")
    void putIntroductionRejectsTraversalImageUrl() throws Exception {
        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .param("sponsors[0].imageUrl", url("intro/../recruit/x.png")))
                .andExpect(status().isBadRequest());
        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .param("sponsors[0].imageUrl", url("intro/%2E%2e/recruit/x.png")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("파일명 안의 ..는 경로 이동이 아니므로 기존 이미지로 유지할 수 있다")
    void putIntroductionAllowsDotsInFileName() throws Exception {
        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .param("sponsors[0].imageUrl", url("intro/uuidlogo..v2.png")))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("내용은 있는데 이름이 없는 목록 항목은 기존 목록을 지우지 않고 400을 반환한다")
    void putIntroductionRejectsUnnamedListItem() throws Exception {
        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .param("teams[0].description", "이름 없이 설명만"))
                .andExpect(status().isBadRequest());

        verify(s3Service, never()).uploadFile(any(), any());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("새 파일을 함께 보내면 기존 이미지 URL은 검증하지 않고 새 파일로 교체한다")
    void putIntroductionPrefersNewFileOverImageUrl() throws Exception {
        givenUploadReturnsFileName();

        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .file(new MockMultipartFile("teams[0].imageFile", "new.jpg", "image/jpeg", "n".getBytes()))
                        .param("teams[0].name", "학부학")
                        .param("teams[0].description", "설명")
                        .param("teams[0].imageUrl", "https://legacy.example.com/old.png"))
                .andExpect(status().isOk());
        clearPersistenceContext();

        mockMvc.perform(get("/admin/introductions"))
                .andExpect(jsonPath("$.data.teams[0].image_link").value(url("intro/new.jpg")));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("목록 인덱스가 비어 생긴 이름 없는 항목은 저장하지 않는다")
    void putIntroductionSkipsSparseListItems() throws Exception {
        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .param("teams[0].name", "학부학")
                        .param("teams[0].description", "설명")
                        .param("teams[2].name", "대외홍보팀")
                        .param("teams[2].description", "설명"))
                .andExpect(status().isOk());
        clearPersistenceContext();

        mockMvc.perform(get("/admin/introductions"))
                .andExpect(jsonPath("$.data.teams.length()").value(2))
                .andExpect(jsonPath("$.data.teams[1].name").value("대외홍보팀"));
    }

    @Test
    @DisplayName("이미 전체 URL로 저장된 이미지 값은 prefix를 다시 붙이지 않는다")
    void getIntroductionKeepsAbsoluteImageUrls() throws Exception {
        introRepository.save(Introduction.builder()
                .bannerCardinal(34L)
                .bannerStatus(BannerStatus.CLOSE)
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
                .andExpect(jsonPath("$.data.design_image_url").value(url("intro/design.png")));
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

    // S3Util을 거치지 않고 prefix와 키 사이에 / 하나를 넣은 기대 URL을 만든다
    private String url(String key) {
        return (s3Prefix.endsWith("/") ? s3Prefix : s3Prefix + "/") + key;
    }

    private void givenUploadReturnsFileName() {
        given(s3Service.uploadFile(any(), eq("intro")))
                .willAnswer(invocation -> "intro/" + ((MultipartFile) invocation.getArgument(0)).getOriginalFilename());
    }

    // 실제 요청처럼 다음 요청이 새 영속성 컨텍스트에서 DB를 읽도록 비운다
    private void clearPersistenceContext() {
        entityManager.flush();
        entityManager.clear();
    }

    private MockMultipartHttpServletRequestBuilder validRequest(
            int cardinal,
            int memberCount,
            int projectCount,
            int universityCount
    ) {
        MockMultipartHttpServletRequestBuilder request = multipart(HttpMethod.PUT, "/admin/introductions");
        request.param("bannerCardinal", String.valueOf(cardinal))
                .param("bannerStatus", "CLOSE")
                .param("memberCount", String.valueOf(memberCount))
                .param("projectCount", String.valueOf(projectCount))
                .param("universityCount", String.valueOf(universityCount))
                .param("introYoutubeLink", "https://www.youtube.com/watch?v=kusitms");
        return request;
    }
}
