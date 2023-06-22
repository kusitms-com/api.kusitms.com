package com.kusitms.website.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class S3Util {
    public static String s3Url;

    @Value("${aws.s3.prefix}")
    public void setS3Url(String s3Url) {
        this.s3Url = s3Url;
    }

}
