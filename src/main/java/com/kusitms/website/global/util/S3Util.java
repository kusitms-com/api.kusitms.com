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
        return s3Url + key;
    }

    // 응답으로 내려간 URL을 다시 받아 기존 이미지를 유지할 때 사용한다. 우리 버킷 URL만 허용한다
    public static String toKey(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        if (!url.startsWith(s3Url)) {
            throw new IllegalArgumentException("기존 이미지 URL이 올바르지 않습니다: " + url);
        }
        return url.substring(s3Url.length());
    }

    public static List<String> toUrls(List<String> keys) {
        return keys.stream().map(S3Util::toUrl).collect(Collectors.toList());
    }
}
