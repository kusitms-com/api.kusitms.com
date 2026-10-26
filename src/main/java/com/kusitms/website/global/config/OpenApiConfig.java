package com.kusitms.website.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        String securitySchemeName = "Bearer Authentication";

        Info info = new Info()
                .title("KUSTIMS 공식 홈페이지 API Document")
                .version("v0.0.1")
                .description(String.join("\n",
                        "KUSITMS 공식 홈페이지 프로젝트의 API 명세서",
                        "",
                        "### 인증",
                        "- `POST /api/auth/signin`으로 받은 accessToken을 `Authorization: Bearer {accessToken}` 헤더로 전달합니다. (오른쪽 Authorize 버튼)",
                        "- accessToken 만료 시 `POST /api/auth/refresh`로 재발급합니다.",
                        "",
                        "### 권한",
                        "- `/admin/**`: MANAGER 이상, `/admin/owner/**`: OWNER만 호출할 수 있습니다.",
                        "- 권한은 로그인 응답의 `authority`(MEMBER/MANAGER/OWNER)로 확인합니다.",
                        "",
                        "### 응답 형식",
                        "- 성공: `{ \"code\": 200, \"message\": \"요청 성공\", \"data\": ... }` (data가 없으면 생략)",
                        "- 실패: `{ \"code\": 4xx/5xx, \"message\": \"사유\" }`. HTTP 상태 코드와 code가 같습니다.",
                        "",
                        "| 상태 | 의미 | FE 처리 |",
                        "|---|---|---|",
                        "| 400 | 잘못된 요청, 비즈니스 규칙 위반 | message를 사용자에게 표시 |",
                        "| 401 | 토큰 없음/만료/잘못된 토큰, 비활성 회원 | 토큰 갱신 또는 재로그인 |",
                        "| 403 | 로그인했지만 권한 부족 | 권한 없음 안내 |",
                        "| 404 | 없는 경로 | - |",
                        "| 500 | 서버 오류 | 공통 오류 안내 |",
                        "",
                        "### 필드 표기",
                        "- 요청 필드는 camelCase입니다. (예외: 학회 소개 수정의 `obLecture[].image_file`)",
                        "- 응답 필드는 API마다 다릅니다. 인증·마이페이지·채팅·멘토링·어드민 회원 API는 camelCase, 프로젝트·후기·블로그·학회 소개 등 공개 콘텐츠 API는 snake_case입니다. 각 응답 스키마의 필드명을 기준으로 구현하세요."));

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, securityScheme))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .info(info);
    }
}