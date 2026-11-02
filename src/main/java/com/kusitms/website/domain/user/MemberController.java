package com.kusitms.website.domain.user;

import com.kusitms.website.domain.user.dto.request.RefreshTokenRequest;
import com.kusitms.website.domain.user.dto.request.SignInRequest;
import com.kusitms.website.domain.user.dto.request.SignUpRequest;
import com.kusitms.website.domain.user.dto.response.CheckIdResponse;
import com.kusitms.website.domain.user.dto.response.CurrentCardinalResponse;
import com.kusitms.website.domain.user.dto.response.SignInResponse;
import com.kusitms.website.global.auth.UserPrincipal;
import com.kusitms.website.global.common.BaseResponse;
import com.kusitms.website.global.docs.ApiErrorExamples;
import com.kusitms.website.global.docs.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 API. 요청·응답 모두 camelCase. 로그아웃을 제외한 모든 API는 토큰 없이 호출한다.")
public class MemberController {

    private final MemberService memberService;

    @Operation(
            summary = "회원가입",
            description = "가입 직후 상태는 승인 대기(PENDING)이며, 운영진(MANAGER 이상)이 승인해야 로그인할 수 있습니다.\n\n"
                    + "multipart/form-data로 전송하며 `signUpRequest` 파트는 **Content-Type: application/json**으로 보내야 합니다. "
                    + "(Swagger UI에서는 이 파트의 Content-Type이 빠질 수 있어 curl/FE에서 테스트를 권장합니다.)\n\n"
                    + "```\n"
                    + "const form = new FormData();\n"
                    + "form.append('signUpRequest', new Blob([JSON.stringify(body)], { type: 'application/json' }));\n"
                    + "form.append('profileImage', file);\n"
                    + "```"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
            encoding = @Encoding(name = "signUpRequest", contentType = MediaType.APPLICATION_JSON_VALUE)
    ))
    @SecurityRequirements
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "가입 성공. data는 생성된 회원 PK(userId)"),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패 또는 중복 아이디. message에 사유가 담김",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class), examples = {
                            @ExampleObject(name = "중복 아이디", value = "{\"code\":400,\"message\":\"이미 사용 중인 아이디입니다.\"}"),
                            @ExampleObject(name = "비밀번호 규칙", value = "{\"code\":400,\"message\":\"영문, 숫자, 특수문자를 포함해 8자 이상이어야 합니다.\"}"),
                            @ExampleObject(name = "비밀번호 불일치", value = "{\"code\":400,\"message\":\"비밀번호가 일치하지 않습니다.\"}"),
                            @ExampleObject(name = "수료증 누락", value = "{\"code\":400,\"message\":\"33기 이하 회원은 수료증을 업로드해 주세요.\"}"),
                            @ExampleObject(name = "이미지 형식", value = "{\"code\":400,\"message\":\"이미지 파일(jpg, png)만 업로드 가능합니다.\"}"),
                            @ExampleObject(name = "이미지 용량", value = "{\"code\":400,\"message\":\"10MB 이하의 이미지만 업로드 가능합니다.\"}")
                    })),
            @ApiResponse(responseCode = "500", description = "이미지 업로드(S3) 실패 등 서버 오류",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class), examples = @ExampleObject(value = ApiErrorExamples.INTERNAL_SERVER_ERROR)))
    })
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<Long>> signup(
            @RequestPart("signUpRequest") @Valid SignUpRequest request,
            @Parameter(description = "프로필 사진 (jpg/jpeg/png, 최대 10MB)", required = true)
            @RequestPart("profileImage") MultipartFile profileImage,
            @Parameter(description = "수료증 이미지 (jpg/jpeg/png, 최대 10MB). cardinal이 33 이하면 필수")
            @RequestPart(value = "certificateImage", required = false) MultipartFile certificateImage
    ) {
        Long userId = memberService.signup(request, profileImage, certificateImage);
        return ResponseEntity.ok(new BaseResponse<>(userId));
    }

    @Operation(
            summary = "로그인",
            description = "발급된 accessToken을 이후 요청의 `Authorization: Bearer {accessToken}` 헤더에 담습니다.\n\n"
                    + "어드민 FE는 `authority`가 `MANAGER` 또는 `OWNER`인 경우에만 어드민 화면 접근을 허용합니다. "
                    + "`role`(YB/OB)은 학회원 구분이며 어드민 권한과 무관합니다."
    )
    @SecurityRequirements
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "아이디/비밀번호 불일치, 승인 대기 또는 탈퇴 계정",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class), examples = {
                            @ExampleObject(name = "없는 아이디", value = "{\"code\":400,\"message\":\"존재하지 않는 아이디입니다.\"}"),
                            @ExampleObject(name = "비밀번호 불일치", value = "{\"code\":400,\"message\":\"비밀번호가 올바르지 않습니다.\"}"),
                            @ExampleObject(name = "승인 대기", value = "{\"code\":400,\"message\":\"관리자 승인 대기 중인 계정입니다. 승인 후 로그인해 주세요.\"}"),
                            @ExampleObject(name = "탈퇴 계정", value = "{\"code\":400,\"message\":\"이미 탈퇴한 계정입니다.\"}"),
                            @ExampleObject(name = "요청 형식 오류", value = ApiErrorExamples.BAD_REQUEST)
                    }))
    })
    @PostMapping("/signin")
    public ResponseEntity<BaseResponse<SignInResponse>> signin(@RequestBody SignInRequest request) {
        SignInResponse response = memberService.signin(request);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    @Operation(summary = "아이디 중복 체크", description = "회원가입 전 아이디 사용 가능 여부를 확인합니다.")
    @SecurityRequirements
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공. 중복이어도 200이며 available로 구분"),
            @ApiResponse(responseCode = "400", description = "아이디 미입력",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(value = "{\"code\":400,\"message\":\"아이디를 먼저 입력해 주세요.\"}")))
    })
    @GetMapping("/check-id")
    public ResponseEntity<BaseResponse<CheckIdResponse>> checkId(
            @Parameter(description = "확인할 로그인 아이디", example = "kusitms34", required = true)
            @RequestParam String id
    ) {
        return ResponseEntity.ok(new BaseResponse<>(memberService.checkIdDuplicate(id)));
    }

    @Operation(
            summary = "토큰 갱신",
            description = "accessToken이 만료(401, \"액세스 토큰이 만료되었습니다...\")되면 refreshToken으로 새 토큰을 발급받습니다.\n\n"
                    + "응답 형식은 로그인과 같으며 **refreshToken도 새 값으로 교체**되므로 저장된 값을 둘 다 갱신해야 합니다. "
                    + "갱신에 실패(400)하면 다시 로그인해야 합니다."
    )
    @SecurityRequirements
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "갱신 성공. 새 accessToken·refreshToken 발급"),
            @ApiResponse(responseCode = "400", description = "유효하지 않거나 만료된 refreshToken (재로그인 필요)",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class), examples = {
                            @ExampleObject(name = "유효하지 않은 토큰", value = "{\"code\":400,\"message\":\"유효하지 않은 리프레시 토큰입니다.\"}"),
                            @ExampleObject(name = "만료된 토큰", value = "{\"code\":400,\"message\":\"리프레시 토큰이 만료되었습니다. 다시 로그인해 주세요.\"}")
                    }))
    })
    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<SignInResponse>> refresh(@RequestBody RefreshTokenRequest request) {
        SignInResponse response = memberService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    @Operation(summary = "로그아웃", description = "서버에 저장된 refreshToken을 삭제합니다. accessToken은 만료 시까지 유효하므로 클라이언트에서도 삭제해야 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공 (data 없음)"),
            @ApiResponse(responseCode = "401", description = "토큰 없음/만료/잘못된 토큰, 또는 승인되지 않았거나 탈퇴한 회원",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class), examples = {
                            @ExampleObject(name = "토큰 없음", value = ApiErrorExamples.UNAUTHORIZED),
                            @ExampleObject(name = "토큰 만료", value = ApiErrorExamples.TOKEN_EXPIRED),
                            @ExampleObject(name = "비활성 회원", value = ApiErrorExamples.INACTIVE_MEMBER)
                    }))
    })
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout() {
        Long userId = getAuthenticatedUserId();
        memberService.logout(userId);
        return ResponseEntity.ok(new BaseResponse<>());
    }

    @Operation(summary = "현재 기수 조회", description = "회원가입 화면의 기수 선택 기준으로 사용합니다.")
    @SecurityRequirements
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/current-cardinal")
    public ResponseEntity<BaseResponse<CurrentCardinalResponse>> getCurrentCardinal() {
        CurrentCardinalResponse response = memberService.getCurrentCardinal();
        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return principal.getPk();
    }
}
