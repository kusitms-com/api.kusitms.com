package com.kusitms.website.domain.user;

import com.kusitms.website.domain.user.dto.request.SignInRequest;
import com.kusitms.website.domain.user.dto.request.SignUpRequest;
import com.kusitms.website.global.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private final MemberRepository memberRepository;

    public Long save(SignUpRequest request) {

        // pw 암호화
        String encodingPassword = bCryptPasswordEncoder.encode(request.getPassword());

        Member user = SignUpRequest.from(request, encodingPassword);

        memberRepository.save(user);
        return user.getUserId();
    }

    public String signin(SignInRequest request) {

        Member user = memberRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("가입된 아이디가 아닙니다."));

        // 패스워드 일치 여부 확인
        passwordMustBeSame(request.getPassword(), user.getPassword());

        // user pk로 토큰 생성
        String token = jwtTokenProvider.makeJwtToken(user.getUserId());

        return token;
    }

    private void passwordMustBeSame(String requestPassword, String password) {
        if (!bCryptPasswordEncoder.matches(requestPassword, password)) {
            throw new IllegalArgumentException();
        }
    }

}
