package com.kusitms.website.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원가입 요청 (multipart의 signUpRequest 파트, Content-Type: application/json)")
public class SignUpRequest {
    @Schema(description = "로그인 아이디 (중복 불가, /api/auth/check-id로 사전 확인)", example = "kusitms34", required = true)
    private String id;

    @Schema(description = "비밀번호. 영문·숫자·특수문자를 각각 포함해 8자 이상", example = "password1!", required = true,
            pattern = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$")
    private String password;

    @Schema(description = "비밀번호 확인 (password와 같아야 함)", example = "password1!", required = true)
    private String passwordConfirm;

    @Schema(description = "이름", example = "홍길동", required = true)
    private String name;

    @Schema(description = "휴대폰 번호. 010으로 시작하는 11자리 (하이픈 포함 가능)", example = "010-1234-5678", required = true)
    private String phone;

    @Schema(description = "활동 기수. 33기 이하는 certificateImage(수료증) 필수", example = "34", required = true)
    private Integer cardinal;

    @Schema(description = "파트", example = "BACKEND", required = true, allowableValues = {"PLANNER", "DESIGNER", "FRONTEND", "BACKEND"})
    private String part;

    @Schema(description = "이메일 (가입 승인/반려 메일 수신)", example = "kusitms@example.com", required = true)
    private String email;
}
