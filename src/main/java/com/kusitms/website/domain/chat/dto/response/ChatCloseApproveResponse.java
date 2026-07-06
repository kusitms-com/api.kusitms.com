package com.kusitms.website.domain.chat.dto.response;

import com.kusitms.website.domain.chat.entity.ChatRoomStatus;
import com.kusitms.website.domain.mentoring.entity.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "채팅 종료 승인 응답")
public class ChatCloseApproveResponse {

    @Schema(description = "채팅방 ID")
    private Long roomId;

    @Schema(description = "채팅방 상태")
    private ChatRoomStatus roomStatus;

    @Schema(description = "멘토링 신청 상태")
    private ApplicationStatus applicationStatus;

    @Schema(description = "종료 버튼 상태")
    private ChatCloseActionStatus closeActionStatus;
}
