package com.kusitms.website.domain.email.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema
public class EmailResponse {
    @Schema(description = "Id")
    private String id;

    @Schema(description = "이메일")
    private String email;
}
