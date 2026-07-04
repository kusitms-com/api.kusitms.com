package com.kusitms.website.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@Schema(description = "비밀번호 변경 요청")
public class PasswordChangeRequest {

    @NotBlank
    @Schema(description = "현재 비밀번호", required = true)
    private String currentPassword;

    @NotBlank
    @Schema(description = "새 비밀번호", required = true)
    private String newPassword;

    @NotBlank
    @Schema(description = "새 비밀번호 확인", required = true)
    private String newPasswordConfirm;
}
