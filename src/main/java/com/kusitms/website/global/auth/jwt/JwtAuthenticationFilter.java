package com.kusitms.website.global.auth.jwt;

import com.kusitms.website.global.common.BaseExceptionStatus;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Getter
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Header에서 token 값 받아오기
        String token = jwtTokenProvider.getJwtFromRequest(request);

        try {
            // 토큰 유효성 검사
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                // 인증 객체 생성
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                // SecurityContextHolder에 인증 객체 저장
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (SecurityException | MalformedJwtException e) {
            request.setAttribute("exception", BaseExceptionStatus.INVALID_AUTH_TOKEN.toString());
        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", BaseExceptionStatus.EXPIRED_AUTH_TOKEN.toString());
        } catch (UnsupportedJwtException e) {
            request.setAttribute("exception", BaseExceptionStatus.UNSUPPORTED_AUTH_TOKEN.toString());
        } catch (IllegalArgumentException e) {
            request.setAttribute("exception", BaseExceptionStatus.WRONG_TOKEN.toString());
        } catch (Exception e) {
            log.error("================================================");
            log.error("JwtFilter - doFilterInternal() 오류발생");
            log.error("token : {}", token);
            log.error("Exception Message : {}", e.getMessage());
            log.error("Exception StackTrace : {");
            e.printStackTrace();
            log.error("}");
            log.error("================================================");
            request.setAttribute("exception", BaseExceptionStatus.INTER_SERVER_ERROR.toString());
        }

        filterChain.doFilter(request, response);
    }
}
