package com.kusitms.website.domain.blog;

import com.kusitms.website.domain.blog.entity.BlogReview;
import com.kusitms.website.domain.blog.entity.BlogReviewActivity;
import com.kusitms.website.domain.blog.repository.BlogReviewRepository;
import com.kusitms.website.domain.file.service.S3Service;
import com.kusitms.website.domain.project.entity.Team;
import com.kusitms.website.domain.user.entity.Member;
import com.kusitms.website.domain.user.entity.MemberAuthority;
import com.kusitms.website.domain.user.entity.MemberRole;
import com.kusitms.website.domain.user.entity.MemberStatus;
import com.kusitms.website.domain.user.repository.MemberRepository;
import com.kusitms.website.global.auth.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BlogApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired EntityManager em;
    @Autowired MemberRepository memberRepository;
    @Autowired JwtTokenProvider jwtTokenProvider;
    @Autowired BlogReviewRepository blogReviewRepository;

    @MockBean S3Service s3Service;

    // CI는 시크릿으로 만든 application-test.properties로 prefix를 덮어쓰므로 주입된 값을 쓴다
    @Value("${aws.s3.prefix}")
    String s3Prefix;

    Member manager;

    @BeforeEach
    void setUp() {
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
    @DisplayName("어드민에서 등록한 블로그 후기가 공개 API에 기존 응답 형식으로 노출된다")
    void adminBlogReviewIsPublic() throws Exception {
        mockMvc.perform(auth(multipart("/admin/blog-review")
                        .file(new MockMultipartFile("thumbnailFile", "thumb.png", "image/png", new byte[]{1}))
                        .param("cardinal", "34")
                        .param("part", "PLANNER")
                        .param("activity", "INTERVIEW_REVIEW")
                        .param("blogLink", "https://blog.example.com/interview")
                        .param("title", "면접 후기")
                        .param("previewText", "면접 준비 과정을 공유합니다.")))
                .andExpect(status().isOk());
        flushAndClear();

        Long id = blogReviewRepository.findAllByOrderByBlogReviewIdDesc().get(0).getBlogReviewId();

        mockMvc.perform(get("/api/blogs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].blog_post_id").value(id))
                .andExpect(jsonPath("$.data.content[0].title").value("면접 후기"))
                .andExpect(jsonPath("$.data.content[0].address").value("https://blog.example.com/interview"))
                .andExpect(jsonPath("$.data.content[0].imageAddress").value(expectedUrl("blog-review/thumb.png")))
                .andExpect(jsonPath("$.data.content[0].content").value("면접 준비 과정을 공유합니다."))
                .andExpect(jsonPath("$.data.content[0].categoryName").value("면접 후기"))
                .andExpect(jsonPath("$.data.content[0].cardinal").value(34))
                .andExpect(jsonPath("$.data.content[0].position").value("기획"));
    }

    @Test
    @DisplayName("어드민에서 수정·삭제한 내용이 공개 API에 반영된다")
    void adminUpdateAndDeleteArePublic() throws Exception {
        BlogReview saved = save(34, Team.BACKEND, BlogReviewActivity.DOCUMENT_REVIEW, "서류 후기");

        mockMvc.perform(auth(multipart("/admin/blog-review").with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .param("blogReviewId", saved.getBlogReviewId().toString())
                        .param("cardinal", "34")
                        .param("part", "BACKEND")
                        .param("activity", "MEETUP_PROJECT")
                        .param("blogLink", "https://blog.example.com/updated")
                        .param("title", "수정된 후기")))
                .andExpect(status().isOk());
        flushAndClear();

        mockMvc.perform(get("/api/blogs"))
                .andExpect(jsonPath("$.data.content[0].title").value("수정된 후기"))
                .andExpect(jsonPath("$.data.content[0].categoryName").value("밋업 프로젝트"))
                .andExpect(jsonPath("$.data.content[0].address").value("https://blog.example.com/updated"));

        mockMvc.perform(auth(delete("/admin/blog-review/" + saved.getBlogReviewId())))
                .andExpect(status().isOk());
        flushAndClear();

        mockMvc.perform(get("/api/blogs"))
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    @DisplayName("홈페이지 필터 값(generation·position·category)을 저장 값으로 변환해 조회한다")
    void filtersAreMapped() throws Exception {
        save(33, Team.PLANNER, BlogReviewActivity.COMPANY_PROJECT, "33기 기획 기업 프로젝트");
        save(34, Team.PLANNER, BlogReviewActivity.ACTIVITY_REVIEW, "34기 기획 활동 후기");
        save(34, Team.DESIGNER, BlogReviewActivity.COMPANY_PROJECT, "34기 디자인 기업 프로젝트");
        flushAndClear();

        mockMvc.perform(get("/api/blogs").param("position", "PLAN"))
                .andExpect(jsonPath("$.data.content[*].title", contains("34기 기획 활동 후기", "33기 기획 기업 프로젝트")));

        mockMvc.perform(get("/api/blogs").param("category", "GIFT").param("generation", "34"))
                .andExpect(jsonPath("$.data.content[*].title", contains("34기 디자인 기업 프로젝트")));

        mockMvc.perform(get("/api/blogs").param("category", "GROUP_TF"))
                .andExpect(jsonPath("$.data.content[*].title", contains("34기 기획 활동 후기")))
                .andExpect(jsonPath("$.data.content[0].categoryName").value("활동 후기"));
    }

    @Test
    @DisplayName("최신순으로 페이지 단위 조회한다")
    void paging() throws Exception {
        for (int i = 1; i <= 3; i++) {
            save(34, Team.BACKEND, BlogReviewActivity.MEETUP_PROJECT, "후기 " + i);
        }
        flushAndClear();

        mockMvc.perform(get("/api/blogs").param("page", "0").param("size", "2"))
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(jsonPath("$.data.totalPages").value(2))
                .andExpect(jsonPath("$.data.content[*].title", contains("후기 3", "후기 2")));
    }

    @Test
    @DisplayName("이관한 외부 이미지 URL은 그대로, 썸네일이 없으면 null, 홈페이지 필터에 없는 파트는 기타로 응답한다")
    void migratedValues() throws Exception {
        blogReviewRepository.save(BlogReview.builder()
                .cardinal(31)
                .part(Team.IOS)
                .activity(BlogReviewActivity.MEETUP_PROJECT)
                .thumbnailUrl("https://image.example.com/thumb.png")
                .title("외부 이미지")
                .build());
        save(31, Team.FRONTEND, BlogReviewActivity.MEETUP_PROJECT, "썸네일 없음");
        flushAndClear();

        mockMvc.perform(get("/api/blogs"))
                .andExpect(jsonPath("$.data.content[0].title").value("썸네일 없음"))
                .andExpect(jsonPath("$.data.content[0].imageAddress").value(nullValue()))
                .andExpect(jsonPath("$.data.content[1].imageAddress").value("https://image.example.com/thumb.png"))
                .andExpect(jsonPath("$.data.content[1].position").value("기타"));
    }

    private BlogReview save(Integer cardinal, Team part, BlogReviewActivity activity, String title) {
        return blogReviewRepository.save(BlogReview.builder()
                .cardinal(cardinal)
                .part(part)
                .activity(activity)
                .title(title)
                .build());
    }

    private String expectedUrl(String key) {
        return (s3Prefix.endsWith("/") ? s3Prefix : s3Prefix + "/") + key;
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    private MockHttpServletRequestBuilder auth(MockHttpServletRequestBuilder request) {
        return request.header("Authorization", "Bearer " + jwtTokenProvider.makeJwtToken(manager.getUserId()));
    }
}
