package com.kusitms.website.domain.email.controller;

import com.kusitms.website.domain.email.dto.request.EmailSaveRequest;
import com.kusitms.website.domain.email.dto.response.EmailResponse;
import com.kusitms.website.domain.email.service.EmailService;
import com.kusitms.website.global.common.BaseResponse;
import com.kusitms.website.global.docs.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Email", description = "모집 알림 이메일 API")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/emails")
    @Operation(
            summary = "모집 알림 이메일 등록",
            description = "학회 모집 소식을 받을 이메일을 등록합니다. 로그인 없이 호출합니다.\n\n"
                    + "요청 본문 `{\"email\": \"...\"}`으로 보내는 것을 권장합니다. "
                    + "기존 방식인 쿼리 파라미터(`?email=`)도 당분간 지원하며, 둘 다 있으면 본문 값을 사용합니다.\n\n"
                    + "이메일은 앞뒤 공백을 제거하고 소문자로 저장하며, 대소문자만 다른 이메일은 중복으로 처리합니다."
    )
    @SecurityRequirements
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "이메일 누락, 형식 오류 또는 이미 등록된 이메일. message를 사용자에게 그대로 표시",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class), examples = {
                            @ExampleObject(name = "중복", value = "{\"code\":400,\"message\":\"이미 등록된 이메일입니다.\"}"),
                            @ExampleObject(name = "형식 오류", value = "{\"code\":400,\"message\":\"유효하지 않은 이메일 형식입니다.\"}"),
                            @ExampleObject(name = "누락", value = "{\"code\":400,\"message\":\"이메일은 필수 입력값입니다.\"}")
                    }))
    })
    public ResponseEntity<BaseResponse<EmailResponse>> saveEmail(
            @RequestBody(required = false) EmailSaveRequest request,
            @Parameter(description = "이전 방식 호환용. 요청 본문 사용을 권장합니다.", deprecated = true)
            @RequestParam(required = false) String email
    ) {
        String value = request != null && request.getEmail() != null ? request.getEmail() : email;
        return ResponseEntity.ok(
                new BaseResponse<>(emailService.saveEmail(value))
        );
    }
}
