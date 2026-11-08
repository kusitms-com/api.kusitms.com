package com.kusitms.website.domain.email;

import com.kusitms.website.domain.email.entity.Email;
import com.kusitms.website.domain.email.repository.EmailRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EmailApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired EmailRepository emailRepository;

    @Test
    @DisplayName("요청 본문으로 등록하면 공백을 제거하고 소문자로 저장한다")
    void saveWithBody() throws Exception {
        mockMvc.perform(post("/api/emails").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"  Kusitms@Example.COM \"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("kusitms@example.com"));

        Email saved = emailRepository.findAll().get(0);
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
}
