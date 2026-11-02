package com.kusitms.website.domain.user.entity;

public enum MemberRole {
    YB,
    OB,
    /**
     * @deprecated 어드민 권한은 {@link MemberAuthority}로 분리했다.
     * 기존 DB 데이터를 읽기 위해서만 남겨두며, 서버 기동 시 MANAGER 권한으로 이전된다.
     */
    @Deprecated
    ADMIN
}
