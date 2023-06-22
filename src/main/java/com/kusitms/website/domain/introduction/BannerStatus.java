package com.kusitms.website.domain.introduction;

public enum BannerStatus {
    CLOSE("모집 마감"),
    MANAGE_RECRUIT("운영진 모집"),
    MEMBER_RECRUIT("학회원 모집")
    ;

    private String name;
    BannerStatus(String name) { this.name = name; }

    public String getName() {
        return name;
    }

    public static BannerStatus from(String name) {
        for(BannerStatus type : BannerStatus.values()) {
            if(type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }
}
