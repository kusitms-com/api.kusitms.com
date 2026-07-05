package com.kusitms.website.domain.user.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class ApplicationRejectRequest {

    @NotBlank(message = "거절 사유를 입력해 주세요.")
    @Size(max = 300, message = "거절 사유는 300자 이하여야 합니다.")
    private String rejectionReason;
}
