package com.kusitms.website.global.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BaseExceptionStatus {
    SUCCESS(HttpStatus.OK, 200, "요청 성공"),
    INTER_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "INTER SERVER ERROR"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, 404, "요청한 리소스를 찾을 수 없습니다."),
    NOT_SUPPORT_FILE(HttpStatus.BAD_REQUEST, 4001, "지원하지 않는 파일 형식입니다"),
    FILE_UPLOAD_FAIL(HttpStatus.BAD_REQUEST, 4002, "파일 업로드 실패"),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED, 401, "인증되지 않은 사용자입니다."),
    INVALID_AUTH_TOKEN(HttpStatus.UNAUTHORIZED ,401, "권한 정보가 없는 토큰입니다"),
    EXPIRED_AUTH_TOKEN(HttpStatus.UNAUTHORIZED, 401, "액세스 토큰이 만료되었습니다. 토큰을 재발급해주세요."),
    UNSUPPORTED_AUTH_TOKEN(HttpStatus.UNAUTHORIZED, 401, "지원되지 않는 토큰입니다."),
    WRONG_TOKEN(HttpStatus.UNAUTHORIZED, 401, "잘못된 형식의 토큰입니다."),
    INACTIVE_MEMBER(HttpStatus.UNAUTHORIZED, 401, "승인되지 않았거나 탈퇴한 회원입니다."),

    /* 403 FORBIDDEN : 인증은 됐지만 권한이 없는 사용자 */
    FORBIDDEN(HttpStatus.FORBIDDEN, 403, "접근 권한이 없습니다."),
    ;

    private HttpStatus httpStatus;
    private int code;
    private String message;

    private BaseExceptionStatus(HttpStatus httpStatus, int code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
