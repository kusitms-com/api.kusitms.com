package com.kusitms.website.domain.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Schema(description = "채팅 메시지 전송 요청")
public class ChatMessageSendRequest {

    @NotBlank(message = "메시지를 입력해 주세요.")
    @Size(max = 1000, message = "메시지는 1000자 이하여야 합니다.")
    @Schema(description = "메시지 내용", required = true, maxLength = 1000)
    private String content;
}
