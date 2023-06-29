package com.kusitms.website.global.auth;

import com.kusitms.website.global.common.BaseExceptionStatus;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String exception = (String)request.getAttribute("exception");

        if(exception == null) {
            setResponse(response, BaseExceptionStatus.ACCESS_DENIED);
        }
        //잘못된 타입의 토큰인 경우
        else if(exception.equals(BaseExceptionStatus.INVALID_AUTH_TOKEN.toString())) {
            setResponse(response, BaseExceptionStatus.INVALID_AUTH_TOKEN);
        }
        //토큰 만료된 경우
        else if(exception.equals(BaseExceptionStatus.EXPIRED_AUTH_TOKEN.toString())) {
            setResponse(response, BaseExceptionStatus.EXPIRED_AUTH_TOKEN);
        }
        //지원되지 않는 토큰인 경우
        else if(exception.equals(BaseExceptionStatus.UNSUPPORTED_AUTH_TOKEN.toString())) {
            setResponse(response, BaseExceptionStatus.UNSUPPORTED_AUTH_TOKEN);
        }
        else {
            setResponse(response, BaseExceptionStatus.WRONG_TOKEN);
        }
    }
    //한글 출력을 위해 getWriter() 사용
    private void setResponse(HttpServletResponse response, BaseExceptionStatus exceptionCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        JSONObject responseJson = new JSONObject();
        responseJson.put("timestamp", LocalDateTime.now());
        responseJson.put("error", exceptionCode.getHttpStatus().toString());
        responseJson.put("message", exceptionCode.getMessage());
        responseJson.put("code", exceptionCode.getCode());

        response.getWriter().print(responseJson);
    }

}