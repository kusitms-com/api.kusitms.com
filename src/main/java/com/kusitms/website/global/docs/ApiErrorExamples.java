package com.kusitms.website.global.docs;

/**
 * Swagger @ExampleObject에 쓰는 에러 응답 예시 모음.
 */
public final class ApiErrorExamples {

    public static final String BAD_REQUEST = "{\"code\":400,\"message\":\"잘못된 요청입니다.\"}";

    public static final String UNAUTHORIZED = "{\"code\":401,\"error\":\"401 UNAUTHORIZED\",\"message\":\"인증되지 않은 사용자입니다.\",\"timestamp\":\"2026-10-26T12:00:00\"}";
    public static final String TOKEN_EXPIRED = "{\"code\":401,\"error\":\"401 UNAUTHORIZED\",\"message\":\"액세스 토큰이 만료되었습니다. 토큰을 재발급해주세요.\",\"timestamp\":\"2026-10-26T12:00:00\"}";
    public static final String INACTIVE_MEMBER = "{\"code\":401,\"error\":\"401 UNAUTHORIZED\",\"message\":\"승인되지 않았거나 탈퇴한 회원입니다.\",\"timestamp\":\"2026-10-26T12:00:00\"}";

    public static final String FORBIDDEN = "{\"code\":403,\"message\":\"접근 권한이 없습니다.\"}";

    public static final String INTERNAL_SERVER_ERROR = "{\"code\":500,\"message\":\"INTER SERVER ERROR\"}";

    private ApiErrorExamples() {
    }
}
