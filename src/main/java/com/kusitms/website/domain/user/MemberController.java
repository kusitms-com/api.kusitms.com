package com.kusitms.website.domain.user;

import com.kusitms.website.domain.user.dto.request.SignInRequest;
import com.kusitms.website.domain.user.dto.request.SignUpRequest;
import com.kusitms.website.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @Hidden
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse> signup (@RequestBody SignUpRequest request) {
        Long userId = memberService.save(request);
        return ResponseEntity.ok(new BaseResponse(userId));
    }

    @PostMapping("/signin")
    public ResponseEntity<BaseResponse> signin (@RequestBody SignInRequest request) {
        String token = memberService.signin(request);
        return ResponseEntity.ok(new BaseResponse(token));
    }
}
