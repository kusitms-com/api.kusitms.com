package com.kusitms.website.domain.introduction;

import com.jayway.jsonpath.JsonPath;
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
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.clearInvocations;
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
                .andExpect(jsonPath("$.data.partner_logos[0].id").isNumber())
                .andExpect(jsonPath("$.data.partner_logos[0].image_url").value(url("intro/verty.png")))
                .andExpect(jsonPath("$.data.teams[0].id").isNumber())
                .andExpect(jsonPath("$.data.teams[0].image_link").value(url("intro/team.jpg")));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("목록은 id 기준으로 수정·추가·삭제하고, 파일을 다시 보내지 않아도 기존 이미지를 유지하며 요청 순서대로 정렬한다")
    void putIntroductionUpdatesListItemsById() throws Exception {
        givenUploadReturnsFileName();

        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .file(new MockMultipartFile("teams[0].imageFile", "team0.jpg", "image/jpeg", "t".getBytes()))
                        .file(new MockMultipartFile("teams[1].imageFile", "team1.jpg", "image/jpeg", "t".getBytes()))
                        .file(new MockMultipartFile("partnerLogos[0].imageFile", "lg.png", "image/png", "l".getBytes()))
                        .file(new MockMultipartFile("partnerLogos[1].imageFile", "verty.png", "image/png", "v".getBytes()))
                        .param("teams[0].name", "학부학")
                        .param("teams[0].description", "기존 설명")
                        .param("teams[1].name", "경영총괄팀")
                        .param("teams[1].description", "삭제될 팀")
                        .param("activities[0].name", "OT")
                        .param("activities[0].description", "오리엔테이션"))
                .andExpect(status().isOk());
        clearPersistenceContext();

        String created = adminIntroduction();
        long teamId = idAt(created, "$.data.teams[0].id");
        long lgId = idAt(created, "$.data.partner_logos[0].id");
        long vertyId = idAt(created, "$.data.partner_logos[1].id");

        // 학부학은 설명만 수정(파일 없이 이미지 유지), 경영총괄팀은 삭제, 새 팀 추가, 로고는 순서 변경 + 새 로고 추가
        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .file(new MockMultipartFile("partnerLogos[2].imageFile", "gabia.png", "image/png", "g".getBytes()))
                        .param("teams[0].id", String.valueOf(teamId))
                        .param("teams[0].name", "학부학")
                        .param("teams[0].description", "수정된 설명")
                        // 폼 상태를 그대로 보내 빈 값이 와도 삭제 요청으로 보지 않는다
                        .param("teams[0].removeImage", "")
                        .param("teams[1].name", "교육기획팀")
                        .param("teams[1].description", "새 팀")
                        .param("partnerLogos[0].id", String.valueOf(vertyId))
                        .param("partnerLogos[1].id", String.valueOf(lgId)))
                .andExpect(status().isOk());
        clearPersistenceContext();

        mockMvc.perform(get("/admin/introductions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.teams.length()").value(2))
                .andExpect(jsonPath("$.data.teams[0].id").value(teamId))
                .andExpect(jsonPath("$.data.teams[0].description").value("수정된 설명"))
                .andExpect(jsonPath("$.data.teams[0].image_link").value(url("intro/team0.jpg")))
                .andExpect(jsonPath("$.data.teams[1].name").value("교육기획팀"))
                .andExpect(jsonPath("$.data.teams[1].image_link").doesNotExist())
                .andExpect(jsonPath("$.data.partner_logos.length()").value(3))
                .andExpect(jsonPath("$.data.partner_logos[0].id").value(vertyId))
                .andExpect(jsonPath("$.data.partner_logos[1].id").value(lgId))
                .andExpect(jsonPath("$.data.partner_logos[2].image_url").value(url("intro/gabia.png")))
                // 요청에서 생략한 목록은 그대로 유지
                .andExpect(jsonPath("$.data.activities[0].name").value("OT"));

        // 공개 응답의 이미지 목록은 기존처럼 URL 배열이고 같은 순서를 따른다
        mockMvc.perform(get("/api/introductions"))
                .andExpect(jsonPath("$.data.partner_logo_urls[0]").value(url("intro/verty.png")))
                .andExpect(jsonPath("$.data.partner_logo_urls[1]").value(url("intro/lg.png")))
                .andExpect(jsonPath("$.data.partner_logo_urls[2]").value(url("intro/gabia.png")));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("기존 항목은 새 파일을 보내면 이미지를 교체하고, removeImage면 이미지를 지운다")
    void putIntroductionReplacesOrRemovesItemImage() throws Exception {
        givenUploadReturnsFileName();

        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .file(new MockMultipartFile("teams[0].imageFile", "old0.jpg", "image/jpeg", "t".getBytes()))
                        .file(new MockMultipartFile("teams[1].imageFile", "old1.jpg", "image/jpeg", "t".getBytes()))
                        .param("teams[0].name", "학부학")
                        .param("teams[1].name", "경영총괄팀"))
                .andExpect(status().isOk());
        clearPersistenceContext();

        String created = adminIntroduction();
        long firstId = idAt(created, "$.data.teams[0].id");
        long secondId = idAt(created, "$.data.teams[1].id");

        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .file(new MockMultipartFile("teams[0].imageFile", "new0.jpg", "image/jpeg", "n".getBytes()))
                        .param("teams[0].id", String.valueOf(firstId))
                        .param("teams[0].name", "학부학")
                        .param("teams[1].id", String.valueOf(secondId))
                        .param("teams[1].name", "경영총괄팀")
                        .param("teams[1].removeImage", "true"))
                .andExpect(status().isOk());
        clearPersistenceContext();

        mockMvc.perform(get("/admin/introductions"))
                .andExpect(jsonPath("$.data.teams[0].image_link").value(url("intro/new0.jpg")))
                .andExpect(jsonPath("$.data.teams[1].image_link").doesNotExist());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("이 소개의 해당 목록에 없는 id나 중복 id는 업로드 전에 400을 반환한다")
    void putIntroductionRejectsInvalidItemId() throws Exception {
        givenUploadReturnsFileName();
        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .file(new MockMultipartFile("partnerLogos[0].imageFile", "lg.png", "image/png", "l".getBytes()))
                        .param("teams[0].name", "학부학"))
                .andExpect(status().isOk());
        clearPersistenceContext();
        long teamId = idAt(adminIntroduction(), "$.data.teams[0].id");
        long logoId = idAt(adminIntroduction(), "$.data.partner_logos[0].id");
        clearInvocations(s3Service);

        // 다른 목록(로고)의 id를 운영진 id로 보낸 경우
        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .file(new MockMultipartFile("planningImage", "planning.png", "image/png", "p".getBytes()))
                        .param("teams[0].id", String.valueOf(teamId + logoId + 1000))
                        .param("teams[0].name", "학부학"))
                .andExpect(status().isBadRequest());

        // 같은 id를 두 번 보낸 경우
        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .param("teams[0].id", String.valueOf(teamId))
                        .param("teams[0].name", "학부학")
                        .param("teams[1].id", String.valueOf(teamId))
                        .param("teams[1].name", "학부학"))
                .andExpect(status().isBadRequest());

        verify(s3Service, never()).uploadFile(any(), any());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("소개를 처음 등록할 때 id를 보내면 400을 반환한다")
    void putIntroductionRejectsIdOnCreate() throws Exception {
        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .param("sponsors[0].id", "1"))
                .andExpect(status().isBadRequest());

        assertThat(introRepository.count()).isZero();
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("내용은 있는데 이름이 없는 목록 항목은 기존 목록을 바꾸지 않고 400을 반환한다")
    void putIntroductionRejectsUnnamedListItem() throws Exception {
        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .param("teams[0].description", "이름 없이 설명만"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .param("activities[0].removeImage2", "true"))
                .andExpect(status().isBadRequest());

        verify(s3Service, never()).uploadFile(any(), any());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("빈 항목 하나만 보내면 해당 목록을 비운다")
    void putIntroductionClearsListWithBlankItem() throws Exception {
        givenUploadReturnsFileName();
        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .file(new MockMultipartFile("sponsors[0].imageFile", "gabia.png", "image/png", "g".getBytes()))
                        .param("teams[0].name", "학부학"))
                .andExpect(status().isOk());
        clearPersistenceContext();

        mockMvc.perform(validRequest(34, 1400, 320, 80)
                        .param("sponsors[0].id", "")
                        .param("teams[0].name", ""))
                .andExpect(status().isOk());
        clearPersistenceContext();

        mockMvc.perform(get("/admin/introductions"))
                .andExpect(jsonPath("$.data.sponsors.length()").value(0))
                .andExpect(jsonPath("$.data.teams.length()").value(0));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("목록 인덱스가 비어 생긴 빈 항목은 저장하지 않는다")
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

    private String adminIntroduction() throws Exception {
        return mockMvc.perform(get("/admin/introductions"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    private static long idAt(String json, String path) {
        return ((Number) JsonPath.read(json, path)).longValue();
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
