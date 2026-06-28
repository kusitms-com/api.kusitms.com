package com.kusitms.website.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원가입 요청")
public class SignUpRequest {
    @Schema(description = "아이디")
    private String id;

    @Schema(description = "비밀번호")
    private String password;

    @Schema(description = "비밀번호 확인")
    private String passwordConfirm;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "휴대폰 번호")
    private String phone;

    @Schema(description = "활동 기수")
    private Integer cardinal;

    @Schema(description = "파트 (PLANNER, DESIGNER, FRONTEND, BACKEND)")
    private String part;

    @Schema(description = "이메일")
    private String email;
}
