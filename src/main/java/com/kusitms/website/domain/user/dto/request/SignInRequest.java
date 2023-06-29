package com.kusitms.website.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema
public class SignInRequest {
    @Schema(description = "관리자 아이디")
    private String id;

    @Schema(description = "관리자 비밀번호")
    private String password;
}
