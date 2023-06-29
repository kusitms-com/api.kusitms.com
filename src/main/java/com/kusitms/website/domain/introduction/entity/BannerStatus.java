package com.kusitms.website.domain.introduction.entity;

public enum BannerStatus {
    CLOSE("모집 마감", "리크루팅 종료"),
    MANAGE_RECRUIT("운영진 모집", "운영진 모집 중"),
    MEMBER_RECRUIT("학회원 모집", "학회원 모집 중")
    ;

    private String name;
    private String content;

    BannerStatus(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }
    public String getContent() { return content; }

    public static BannerStatus from(String name) {
        for(BannerStatus type : BannerStatus.values()) {
            if(type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }
}
