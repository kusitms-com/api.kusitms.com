package com.kusitms.website.domain.user;

import com.kusitms.website.domain.user.dto.request.SignInRequest;
import com.kusitms.website.domain.user.dto.request.SignUpRequest;
import com.kusitms.website.domain.user.dto.response.CurrentCardinalResponse;
import com.kusitms.website.domain.user.dto.response.SignInResponse;
import com.kusitms.website.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 API")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원가입")
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> signup(
            @RequestPart("signUpRequest") @Valid SignUpRequest request,
            @RequestPart("profileImage") MultipartFile profileImage,
            @RequestPart(value = "certificateImage", required = false) MultipartFile certificateImage
    ) {
        Long userId = memberService.signup(request, profileImage, certificateImage);
        return ResponseEntity.ok(new BaseResponse(userId));
    }

    @Operation(summary = "로그인")
    @PostMapping("/signin")
    public ResponseEntity<BaseResponse<SignInResponse>> signin(@RequestBody SignInRequest request) {
        SignInResponse response = memberService.signin(request);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    @Operation(summary = "아이디 중복 체크")
    @GetMapping("/check-id")
    public ResponseEntity<BaseResponse<Map<String, Object>>> checkId(@RequestParam String id) {
        Map<String, Object> result = memberService.checkIdDuplicate(id);
        return ResponseEntity.ok(new BaseResponse<>(result));
    }

    @Operation(summary = "토큰 갱신")
    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<SignInResponse>> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        SignInResponse response = memberService.refreshToken(refreshToken);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    @Operation(summary = "현재 기수 조회")
    @GetMapping("/current-cardinal")
    public ResponseEntity<BaseResponse<CurrentCardinalResponse>> getCurrentCardinal() {
        CurrentCardinalResponse response = memberService.getCurrentCardinal();
        return ResponseEntity.ok(new BaseResponse<>(response));
    }
}
