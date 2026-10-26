package com.kusitms.website.global.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Swagger 문서용 에러 응답 스키마. 실제 응답은 BaseResponse 또는 인증 필터가 직접 작성한다.
 */
@Getter
@Schema(name = "ApiErrorResponse", description = "에러 응답 공통 형식. 모든 에러는 code, message를 가진다. 401/403은 인증 필터에서 응답해 error(또는 status), timestamp 필드가 추가된다.")
public class ApiErrorResponse {
    @Schema(description = "에러 코드 (HTTP 상태 코드와 동일)", example = "400")
    private int code;

    @Schema(description = "에러 메시지 (사용자에게 그대로 노출 가능)", example = "잘못된 요청입니다.")
    private String message;
}
