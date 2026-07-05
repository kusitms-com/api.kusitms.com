package com.kusitms.website.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@Schema(description = "OB 프로필 공개 토글 요청")
public class OBProfileVisibilityUpdateRequest {

    @NotNull
    @Schema(description = "멘토링 신청 받기 여부", required = true)
    private Boolean enabled;
}
