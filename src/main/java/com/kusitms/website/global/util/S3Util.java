package com.kusitms.website.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class S3Util {
    public static String s3Url;

    @Value("${aws.s3.prefix}")
    public void setS3Url(String s3Url) {
        this.s3Url = s3Url;
    }

    // DB에는 S3 키만 저장하므로 응답에서 전체 URL로 바꾼다. 업로드하지 않은 이미지는 null 유지
    // DB에 직접 넣은 값처럼 이미 전체 URL이면 prefix가 중복되지 않도록 그대로 반환한다
    public static String toUrl(String key) {
        if (key == null || key.startsWith("http://") || key.startsWith("https://")) {
            return key;
        }
        // 운영 prefix는 /로 끝나지 않고, 키는 업로드분(intro/...)과 수동 입력분(/meetup/...)이 섞여 있어 /를 하나로 맞춘다
        return baseUrl() + (key.startsWith("/") ? key.substring(1) : key);
    }

    // 응답으로 내려간 URL을 다시 받아 기존 이미지를 유지할 때 사용한다. 우리 버킷 URL만 허용한다
    public static String toKey(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        String baseUrl = baseUrl();
        if (!url.startsWith(baseUrl)) {
            throw new IllegalArgumentException("기존 이미지 URL이 올바르지 않습니다: " + url);
        }
        return url.substring(baseUrl.length());
    }

    private static String baseUrl() {
        return s3Url.endsWith("/") ? s3Url : s3Url + "/";
    }

    public static List<String> toUrls(List<String> keys) {
        return keys.stream().map(S3Util::toUrl).collect(Collectors.toList());
    }
}
