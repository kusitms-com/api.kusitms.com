package com.kusitms.website.domain.user.dto.request;

import com.kusitms.website.domain.user.Member;
import lombok.Getter;

@Getter
public class SignUpRequest {

    private String id;
    private String password;

    public static Member from(SignUpRequest request, String encodingPassword) {
        return Member.builder()
                .id(request.getId())
                .password(encodingPassword)
                .build();
    }
}
