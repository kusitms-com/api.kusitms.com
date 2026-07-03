package com.kusitms.website.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@Schema(description = "계정 정보 수정 요청")
public class AccountProfileUpdateRequest {

    @NotBlank
    @Schema(description = "이름", required = true)
    private String name;

    @NotBlank
    @Schema(description = "전화번호", required = true)
    private String phone;
}
