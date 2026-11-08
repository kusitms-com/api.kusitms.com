package com.kusitms.website.domain.admin.controller;

import com.kusitms.website.domain.email.dto.response.AdminEmailResponse;
import com.kusitms.website.domain.email.service.EmailService;
import com.kusitms.website.global.common.BaseResponse;
import com.kusitms.website.global.docs.ApiErrorExamples;
import com.kusitms.website.global.docs.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/emails")
@PreAuthorize("hasRole('MANAGER')")
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "토큰 없음/만료, 또는 비활성 회원",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class), examples = @ExampleObject(value = ApiErrorExamples.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = "MANAGER 미만 권한",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class), examples = @ExampleObject(value = ApiErrorExamples.FORBIDDEN)))
})
@Tag(name = "Admin Email", description = "모집 알림 이메일 관리 API (MANAGER 이상). 응답은 camelCase")
public class AdminEmailController {
    private static final MediaType TEXT_CSV = new MediaType("text", "csv", StandardCharsets.UTF_8);

    private final EmailService emailService;

    @GetMapping
    @Operation(summary = "모집 알림 이메일 목록 조회", description = "수집된 이메일 전체를 최근 등록순으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    public ResponseEntity<BaseResponse<List<AdminEmailResponse>>> getEmails() {
        return ResponseEntity.ok(new BaseResponse<>(emailService.getEmails()));
    }

    @GetMapping(value = "/export", produces = "text/csv")
    @Operation(
            summary = "모집 알림 이메일 CSV 다운로드",
            description = "수집된 이메일 전체를 CSV 파일(`recruit-emails-yyyyMMdd.csv`)로 내려줍니다. "
                    + "열은 `email,created_at`이며 Excel에서 한글이 깨지지 않도록 UTF-8 BOM을 포함합니다.\n\n"
                    + "FE에서는 응답을 blob으로 받아 다운로드합니다. (Authorization 헤더가 필요하므로 a 태그 링크로는 받을 수 없습니다)"
    )
    @ApiResponse(responseCode = "200", description = "CSV 파일",
            content = @Content(mediaType = "text/csv", examples = @ExampleObject(value = "email,created_at\nkusitms@example.com,2026-11-08T14:30:00\n")))
    public ResponseEntity<byte[]> exportEmails() {
        String filename = "recruit-emails-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".csv";
        return ResponseEntity.ok()
                .contentType(TEXT_CSV)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(filename).build().toString())
                .body(emailService.exportEmailsAsCsv());
    }

    @DeleteMapping("/{emailId}")
    @Operation(summary = "모집 알림 이메일 삭제", description = "수신 거부 요청 등으로 이메일을 삭제합니다. 삭제 후 같은 이메일로 다시 등록할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공 (data 없음)"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 이메일 ID",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(value = "{\"code\":404,\"message\":\"존재하지 않는 이메일입니다.\"}")))
    })
    public ResponseEntity<BaseResponse<Void>> deleteEmail(@PathVariable Long emailId) {
        emailService.deleteEmail(emailId);
        return ResponseEntity.ok(new BaseResponse<>());
    }
}
