package com.kusitms.website.domain.user.entity;

/**
 * 어드민 권한. 학회원 구분(YB/OB, {@link MemberRole})과는 별개로 관리한다.
 * 상위 권한은 하위 권한을 포함한다: OWNER > MANAGER > MEMBER (SecurityConfig의 RoleHierarchy 참고)
 */
public enum MemberAuthority {
    MEMBER,
    MANAGER,
    OWNER;

    public String toRoleName() {
        return "ROLE_" + name();
    }
}
