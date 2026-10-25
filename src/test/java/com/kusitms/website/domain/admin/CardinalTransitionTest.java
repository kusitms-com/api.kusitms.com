package com.kusitms.website.domain.admin;

import com.kusitms.website.domain.file.S3Service;
import com.kusitms.website.domain.user.CurrentCardinalRepository;
import com.kusitms.website.domain.user.Member;
import com.kusitms.website.domain.user.MemberAuthority;
import com.kusitms.website.domain.user.MemberRepository;
import com.kusitms.website.domain.user.MemberRole;
import com.kusitms.website.domain.user.MemberService;
import com.kusitms.website.domain.user.MemberStatus;
import com.kusitms.website.domain.user.dto.request.SignUpRequest;
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
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CardinalTransitionTest {

    @Autowired MockMvc mockMvc;
    @Autowired MemberRepository memberRepository;
    @Autowired MemberService memberService;
    @Autowired CurrentCardinalRepository currentCardinalRepository;
    @Autowired JwtTokenProvider jwtTokenProvider;

    @MockBean S3Service s3Service;

    Member owner;
    Member manager;
    Member yb33;
    Member yb34;
    Member ob30;
    Member withdrawn;

    @BeforeEach
    void setUp() {
        // 기동 시 CurrentCardinalInitializer가 기본값 33으로 현재 기수 행을 만든다
        owner = save("owner", 30, MemberStatus.APPROVED, MemberRole.OB, MemberAuthority.OWNER);
        manager = save("manager", 34, MemberStatus.APPROVED, MemberRole.YB, MemberAuthority.MANAGER);
        yb33 = save("yb33", 33, MemberStatus.APPROVED, MemberRole.YB, MemberAuthority.MEMBER);
        yb34 = save("yb34", 34, MemberStatus.PENDING, MemberRole.YB, MemberAuthority.MEMBER);
        ob30 = save("ob30", 30, MemberStatus.APPROVED, MemberRole.OB, MemberAuthority.MEMBER);
        withdrawn = save("withdrawn", 33, MemberStatus.APPROVED, MemberRole.YB, MemberAuthority.MEMBER);
        withdrawn.withdraw();
        memberRepository.flush();
    }

    @Test
    @DisplayName("현재 기수를 변경하면 전체 회원의 YB/OB가 바로 재계산되고, 탈퇴 회원은 제외된다")
    void updateCurrentCardinal() throws Exception {
        mockMvc.perform(get("/api/auth/current-cardinal"))
                .andExpect(jsonPath("$.data.currentCardinal").value(33));

        mockMvc.perform(auth(put("/admin/owner/current-cardinal"), owner)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"cardinal\":34}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.previousCardinal").value(33))
                .andExpect(jsonPath("$.data.currentCardinal").value(34))
                .andExpect(jsonPath("$.data.changedToOb").value(1))
                .andExpect(jsonPath("$.data.changedToYb").value(0));

        mockMvc.perform(get("/api/auth/current-cardinal"))
                .andExpect(jsonPath("$.data.currentCardinal").value(34));
        assertThat(yb33.getRole()).isEqualTo(MemberRole.OB);
        assertThat(yb34.getRole()).isEqualTo(MemberRole.YB);
        assertThat(manager.getRole()).isEqualTo(MemberRole.YB);
        assertThat(ob30.getRole()).isEqualTo(MemberRole.OB);
        assertThat(withdrawn.getRole()).isNull();
    }

    @Test
    @DisplayName("기수를 되돌리면 해당 기수 회원이 다시 YB가 된다")
    void rollbackCurrentCardinal() throws Exception {
        mockMvc.perform(auth(put("/admin/owner/current-cardinal"), owner)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"cardinal\":34}"))
                .andExpect(status().isOk());
        mockMvc.perform(auth(put("/admin/owner/current-cardinal"), owner)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"cardinal\":33}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.changedToYb").value(1));

        assertThat(yb33.getRole()).isEqualTo(MemberRole.YB);
    }

    @Test
    @DisplayName("기수 변경은 OWNER만 가능하고(학회원·운영진 403), 잘못된 기수는 400")
    void authorizationAndValidation() throws Exception {
        mockMvc.perform(auth(put("/admin/owner/current-cardinal"), yb33)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"cardinal\":34}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(auth(put("/admin/owner/current-cardinal"), manager)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"cardinal\":34}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(auth(put("/admin/owner/current-cardinal"), owner)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"cardinal\":0}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(auth(put("/admin/owner/current-cardinal"), owner)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("현재 기수는 다음 기수 또는 직전 기수로만 바꿀 수 있다 (오입력 방지)")
    void onlyAdjacentCardinalAllowed() throws Exception {
        // 현재 33기: 340(오타), 35(건너뛰기)는 거절
        for (String cardinal : new String[]{"340", "35"}) {
            mockMvc.perform(auth(put("/admin/owner/current-cardinal"), owner)
                            .contentType(MediaType.APPLICATION_JSON).content("{\"cardinal\":" + cardinal + "}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("현재 기수는 33기입니다. 34기 또는 32기로만 변경할 수 있습니다."));
        }
        assertThat(yb33.getRole()).isEqualTo(MemberRole.YB);
    }

    @Test
    @DisplayName("같은 기수로 다시 요청하면 변경 없이 성공한다 (재시도 안전)")
    void sameCardinalIsNoOp() throws Exception {
        mockMvc.perform(auth(put("/admin/owner/current-cardinal"), owner)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"cardinal\":33}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.previousCardinal").value(33))
                .andExpect(jsonPath("$.data.currentCardinal").value(33))
                .andExpect(jsonPath("$.data.changedToOb").value(0))
                .andExpect(jsonPath("$.data.changedToYb").value(0));
        assertThat(yb33.getRole()).isEqualTo(MemberRole.YB);
    }

    @Test
    @DisplayName("현재 기수 값이 비어 있으면 ±1 제한 없이 설정할 수 있다")
    void emptyCardinalCanBeFixed() throws Exception {
        currentCardinalRepository.findTopByOrderByIdAsc().orElseThrow().updateCardinal(null);
        currentCardinalRepository.flush();

        mockMvc.perform(auth(put("/admin/owner/current-cardinal"), owner)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"cardinal\":34}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentCardinal").value(34));
        assertThat(yb33.getRole()).isEqualTo(MemberRole.OB);
    }

    @Test
    @DisplayName("수료증은 현재 기수보다 이전 기수(OB) 가입자에게만 필수다")
    void certificateRequiredBeforeCurrentCardinal() throws Exception {
        // 현재 33기: 33기 가입자는 수료증 없이 가입 가능
        assertThat(memberService.signup(signUpRequest("new33", 33), profileImage(), null)).isNotNull();

        mockMvc.perform(auth(put("/admin/owner/current-cardinal"), owner)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"cardinal\":34}"))
                .andExpect(status().isOk());

        // 현재 34기: 33기 가입자는 수료증 필수, 34기 가입자는 불필요
        assertThatThrownBy(() -> memberService.signup(signUpRequest("late33", 33), profileImage(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("33기 이하 회원은 수료증을 업로드해 주세요.");
        assertThat(memberService.signup(signUpRequest("new34", 34), profileImage(), null)).isNotNull();
    }

    private SignUpRequest signUpRequest(String id, int cardinal) {
        SignUpRequest request = new SignUpRequest();
        request.setId(id);
        request.setPassword("password1!");
        request.setPasswordConfirm("password1!");
        request.setName(id);
        request.setPhone("01012345678");
        request.setCardinal(cardinal);
        request.setPart("BACKEND");
        request.setEmail(id + "@test.com");
        return request;
    }

    private MockMultipartFile profileImage() {
        return new MockMultipartFile("profileImage", "profile.png", "image/png", new byte[]{1});
    }

    private Member save(String id, int cardinal, MemberStatus status, MemberRole role, MemberAuthority authority) {
        Member member = Member.builder()
                .id(id)
                .password("password")
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
