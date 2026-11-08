package com.kusitms.website.domain.email;

import com.kusitms.website.domain.email.entity.Email;
import com.kusitms.website.domain.email.repository.EmailRepository;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EmailApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired EmailRepository emailRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired JwtTokenProvider jwtTokenProvider;

    Member member;
    Member manager;

    @BeforeEach
    void setUp() {
        member = saveMember("yb", MemberAuthority.MEMBER);
        manager = saveMember("manager", MemberAuthority.MANAGER);
    }

    @Test
    @DisplayName("요청 본문으로 등록하면 공백을 제거하고 소문자로 저장한다")
    void saveWithBody() throws Exception {
        mockMvc.perform(post("/api/emails").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"  Kusitms@Example.COM \"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("kusitms@example.com"));

        Email saved = emailRepository.findAllByOrderByIdDesc().get(0);
        assertThat(saved.getEmail()).isEqualTo("kusitms@example.com");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("기존 쿼리 파라미터 방식도 계속 동작한다")
    void saveWithQueryParam() throws Exception {
        mockMvc.perform(post("/api/emails").param("email", "legacy@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("legacy@example.com"));
    }

    @Test
    @DisplayName("대소문자만 다른 이메일은 중복으로 400, 정규화 이전 데이터와도 비교한다")
    void duplicateIgnoringCase() throws Exception {
        emailRepository.saveAndFlush(Email.builder().email("Old@Example.com").build());

        mockMvc.perform(post("/api/emails").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"old@example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 등록된 이메일입니다."));
    }

    @Test
    @DisplayName("형식 오류와 누락은 400")
    void invalidEmail() throws Exception {
        mockMvc.perform(post("/api/emails").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"not-an-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("유효하지 않은 이메일 형식입니다."));
        mockMvc.perform(post("/api/emails"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이메일은 필수 입력값입니다."));
    }

    @Test
    @DisplayName("어드민 목록: 비로그인 401, 학회원 403, 운영진은 최근 등록순으로 조회")
    void adminList() throws Exception {
        emailRepository.saveAndFlush(Email.builder().email("first@example.com").build());
        emailRepository.saveAndFlush(Email.builder().email("second@example.com").build());

        mockMvc.perform(get("/admin/emails")).andExpect(status().isUnauthorized());
        mockMvc.perform(auth(get("/admin/emails"), member)).andExpect(status().isForbidden());
        mockMvc.perform(auth(get("/admin/emails"), manager))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].email").value("second@example.com"))
                .andExpect(jsonPath("$.data[0].createdAt").exists());
    }

    @Test
    @DisplayName("CSV 다운로드: 첨부 파일, BOM과 헤더 포함, 수식으로 해석될 값은 이스케이프")
    void exportCsv() throws Exception {
        emailRepository.saveAndFlush(Email.builder().email("kusitms@example.com").build());
        emailRepository.saveAndFlush(Email.builder().email("-risky@example.com").build());

        mockMvc.perform(auth(get("/admin/emails/export"), member)).andExpect(status().isForbidden());

        String csv = mockMvc.perform(auth(get("/admin/emails/export"), manager))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", startsWith("text/csv")))
                .andExpect(header().string("Content-Disposition", containsString("attachment; filename=\"recruit-emails-")))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(csv).startsWith("﻿email,created_at\n");
        assertThat(csv).contains("\n'-risky@example.com,");
        assertThat(csv).contains("\nkusitms@example.com,");
    }

    @Test
    @DisplayName("삭제: 운영진만 가능, 없는 ID는 404, 삭제 후 다시 등록할 수 있다")
    void deleteEmail() throws Exception {
        Email email = emailRepository.saveAndFlush(Email.builder().email("bye@example.com").build());

        mockMvc.perform(auth(delete("/admin/emails/" + email.getId()), member)).andExpect(status().isForbidden());
        mockMvc.perform(auth(delete("/admin/emails/" + email.getId()), manager)).andExpect(status().isOk());
        mockMvc.perform(auth(delete("/admin/emails/" + email.getId()), manager))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 이메일입니다."));

        mockMvc.perform(post("/api/emails").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"bye@example.com\"}"))
                .andExpect(status().isOk());
    }

    private Member saveMember(String id, MemberAuthority authority) {
        Member saved = Member.builder()
                .id(id)
                .password("password")
                .name(id)
                .cardinal(34)
                .status(MemberStatus.APPROVED)
                .role(MemberRole.YB)
                .build();
        saved.updateAuthority(authority);
        return memberRepository.saveAndFlush(saved);
    }

    private MockHttpServletRequestBuilder auth(MockHttpServletRequestBuilder request, Member member) {
        return request.header("Authorization", "Bearer " + jwtTokenProvider.makeJwtToken(member.getUserId()));
    }
}
