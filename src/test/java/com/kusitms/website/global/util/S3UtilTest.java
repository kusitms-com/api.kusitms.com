package com.kusitms.website.global.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @ParameterizedTest(name = "prefix={0}")
    @CsvSource({"https://bucket.test", "https://bucket.test/"})
    @DisplayName("toUrl로 만든 URL은 toKey로 원래 키로 되돌린다")
    void toKeyRoundTrips(String prefix) {
        S3Util.s3Url = prefix;

        assertThat(S3Util.toKey(S3Util.toUrl("intro/a.png"))).isEqualTo("intro/a.png");
    }

    @ParameterizedTest(name = "url={0}")
    @CsvSource({"https://bucket.testintro/a.png", "https://other.test/intro/a.png"})
    @DisplayName("구분자가 빠졌거나 다른 호스트인 URL은 키로 바꾸지 않는다")
    void toKeyRejectsMalformedUrl(String url) {
        S3Util.s3Url = "https://bucket.test";

        assertThatThrownBy(() -> S3Util.toKey(url)).isInstanceOf(IllegalArgumentException.class);
    }
}
