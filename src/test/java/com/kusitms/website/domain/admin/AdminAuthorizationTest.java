package com.kusitms.website.domain.admin;

import com.kusitms.website.domain.admin.service.AdminAuthorityInitializer;
import com.kusitms.website.domain.admin.service.MemberAuthorityService;
import com.kusitms.website.domain.user.entity.Member;
import com.kusitms.website.domain.user.entity.MemberAuthority;
import com.kusitms.website.domain.user.repository.MemberRepository;
import com.kusitms.website.domain.user.entity.MemberRole;
import com.kusitms.website.domain.user.entity.MemberStatus;
import com.kusitms.website.global.auth.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminAuthorizationTest {

    private static final String PASSWORD = "password1!";

    @Autowired MockMvc mockMvc;
    @Autowired MemberRepository memberRepository;
    @Autowired JwtTokenProvider jwtTokenProvider;
    @Autowired BCryptPasswordEncoder passwordEncoder;
    @Autowired MemberAuthorityService memberAuthorityService;
    @Autowired AdminAuthorityInitializer adminAuthorityInitializer;

    Member yb;
    Member manager;
    Member owner;

    @BeforeEach
    void setUp() {
        yb = save("yb", 34, MemberStatus.APPROVED, MemberRole.YB, MemberAuthority.MEMBER);
        manager = save("manager", 34, MemberStatus.APPROVED, MemberRole.YB, MemberAuthority.MANAGER);
        owner = save("owner", 30, MemberStatus.APPROVED, MemberRole.OB, MemberAuthority.OWNER);
    }

    @Test
    @DisplayName("어드민 콘텐츠 API: 비로그인 401, 학회원 403, 운영진/OWNER 200")
    void adminContentApi() throws Exception {
        mockMvc.perform(get("/admin/review")).andExpect(status().isUnauthorized());
        mockMvc.perform(auth(get("/admin/review"), yb))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
        mockMvc.perform(auth(delete("/admin/review/1"), yb)).andExpect(status().isForbidden());
        mockMvc.perform(auth(get("/admin/review"), manager)).andExpect(status().isOk());
        // RoleHierarchy: OWNER는 MANAGER 권한을 포함한다
        mockMvc.perform(auth(get("/admin/review"), owner)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원 목록 API는 운영진 이상만 조회할 수 있고 권한 정보를 포함한다")
    void memberListApi() throws Exception {
        mockMvc.perform(auth(get("/admin/members"), yb)).andExpect(status().isForbidden());
        mockMvc.perform(auth(get("/admin/members").param("status", "APPROVED"), manager))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[?(@.id == 'owner')].authority").value("OWNER"));
    }

    @Test
    @DisplayName("권한 변경 API는 OWNER만 호출할 수 있다")
    void ownerApi() throws Exception {
        String body = "{\"authority\":\"MANAGER\"}";
        String url = "/admin/owner/members/" + yb.getUserId() + "/authority";

        mockMvc.perform(auth(patch(url), manager).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
        mockMvc.perform(auth(patch(url), owner).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());

        assertThat(memberRepository.findById(yb.getUserId()).orElseThrow().getAuthority())
                .isEqualTo(MemberAuthority.MANAGER);
        // 권한은 매 요청 DB에서 읽으므로, 기존 토큰으로도 바로 어드민 API를 쓸 수 있다
        mockMvc.perform(auth(get("/admin/review"), yb)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("OWNER는 자기 자신의 권한을 바꿀 수 없다")
    void ownerCannotChangeSelf() throws Exception {
        mockMvc.perform(auth(patch("/admin/owner/members/" + owner.getUserId() + "/authority"), owner)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"authority\":\"MEMBER\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("마지막 OWNER는 강등할 수 없고, OWNER가 여러 명이면 강등할 수 있다")
    void lastOwnerProtection() {
        assertThatThrownBy(() -> memberAuthorityService.changeAuthority(manager.getUserId(), owner.getUserId(), MemberAuthority.MEMBER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("마지막 OWNER");

        Member secondOwner = save("owner2", 30, MemberStatus.APPROVED, MemberRole.OB, MemberAuthority.OWNER);
        memberAuthorityService.changeAuthority(secondOwner.getUserId(), owner.getUserId(), MemberAuthority.MANAGER);
        assertThat(owner.getAuthority()).isEqualTo(MemberAuthority.MANAGER);
    }

    @Test
    @DisplayName("승인되지 않은 회원에게는 권한을 줄 수 없다")
    void cannotGrantToPendingMember() {
        Member pending = save("pending", 34, MemberStatus.PENDING, MemberRole.YB, MemberAuthority.MEMBER);
        assertThatThrownBy(() -> memberAuthorityService.changeAuthority(owner.getUserId(), pending.getUserId(), MemberAuthority.MANAGER))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("로그인 필요 API는 401, 공개 API는 토큰 없이 접근 가능")
    void memberAndPublicApi() throws Exception {
        mockMvc.perform(get("/api/mypage/account")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/auth/current-cardinal")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("탈퇴한 회원의 토큰은 만료 전이라도 401")
    void withdrawnMemberTokenRejected() throws Exception {
        String token = jwtTokenProvider.makeJwtToken(manager.getUserId());
        manager.withdraw();
        memberRepository.flush();

        mockMvc.perform(get("/admin/review").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("승인되지 않았거나 탈퇴한 회원입니다."));
    }

    @Test
    @DisplayName("기수가 있는 운영진이 로그인해도 MANAGER 권한이 유지되고, role은 YB/OB로 판별된다")
    void signInKeepsAuthority() throws Exception {
        mockMvc.perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"manager\",\"password\":\"" + PASSWORD + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("YB"))
                .andExpect(jsonPath("$.data.authority").value("MANAGER"));
    }

    @Test
    @DisplayName("기존 role=ADMIN 회원은 기동 시 MANAGER 권한으로 이전된다")
    @SuppressWarnings("deprecation")
    void legacyAdminMigration() throws Exception {
        Member legacy = save("legacy", 30, MemberStatus.APPROVED, MemberRole.ADMIN, null);

        adminAuthorityInitializer.run(null);

        assertThat(legacy.getAuthority()).isEqualTo(MemberAuthority.MANAGER);
        assertThat(legacy.getRole()).isEqualTo(MemberRole.OB);
    }

    private Member save(String id, int cardinal, MemberStatus status, MemberRole role, MemberAuthority authority) {
        Member member = Member.builder()
                .id(id)
                .password(passwordEncoder.encode(PASSWORD))
                .name(id)
                .cardinal(cardinal)
                .status(status)
                .role(role)
                .build();
        member.updateAuthority(authority);
        return memberRepository.saveAndFlush(member);
    }

    private MockHttpServletRequestBuilder auth(MockHttpServletRequestBuilder request, Member member) {
        return request.header("Authorization", "Bearer " + jwtTokenProvider.makeJwtToken(member.getUserId()));
    }
}
