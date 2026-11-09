package com.kusitms.website.global.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class S3UtilTest {

    private String originalPrefix;

    @BeforeEach
    void savePrefix() {
        originalPrefix = S3Util.s3Url;
    }

    @AfterEach
    void restorePrefix() {
        S3Util.s3Url = originalPrefix;
    }

    @ParameterizedTest(name = "prefix={0}, key={1}")
    @CsvSource({
            "https://bucket.test, intro/a.png",
            "https://bucket.test/, intro/a.png",
            "https://bucket.test, /intro/a.png",
            "https://bucket.test/, /intro/a.png"
    })
    @DisplayName("prefix 끝과 키 앞의 / 유무와 관계없이 /를 하나만 넣어 URL을 만든다")
    void toUrlJoinsWithSingleSlash(String prefix, String key) {
        S3Util.s3Url = prefix;

        assertThat(S3Util.toUrl(key)).isEqualTo("https://bucket.test/intro/a.png");
    }

    @ParameterizedTest(name = "key=[{0}]")
    @NullSource
    @ValueSource(strings = {"", "   "})
    @DisplayName("키가 없거나 비어 있으면 버킷 루트 URL이 아니라 null을 반환한다")
    void toUrlReturnsNullForBlankKey(String key) {
        S3Util.s3Url = "https://bucket.test";

        assertThat(S3Util.toUrl(key)).isNull();
    }

    @Test
    @DisplayName("prefix가 주입되지 않은 환경에서도 예외 없이 동작한다")
    void toUrlWithoutPrefix() {
        S3Util.s3Url = null;

        assertThat(S3Util.toUrl("intro/a.png")).isEqualTo("/intro/a.png");
    }

}
