package com.kusitms.website.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "로그인 요청")
public class SignInRequest {
    @Schema(description = "로그인 아이디", example = "owner", required = true)
    private String id;

    @Schema(description = "비밀번호", example = "password1!", required = true)
    private String password;
}
