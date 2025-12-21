package com.kusitms.website.domain.email.controller;

import com.kusitms.website.domain.email.dto.response.EmailResponse;
import com.kusitms.website.domain.email.service.EmailService;
import com.kusitms.website.domain.introduction.IntroService;
import com.kusitms.website.domain.introduction.dto.response.IntroResponse;
import com.kusitms.website.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Email", description = "이메일 API Document")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/emails")
    @Operation(summary = "이메일 저장 API", description = "학회 모집 소식 전송 이메일을 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "저장 성공",
                    content = @Content(schema = @Schema(implementation = EmailResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (이메일 형식 오류, 중복 이메일 등)",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            )
    })
    public ResponseEntity<BaseResponse<EmailResponse>> saveEmail(
            @RequestParam String email
    ) {
        return ResponseEntity.ok(
                new BaseResponse<>(emailService.saveEmail(email))
        );
    }
}
