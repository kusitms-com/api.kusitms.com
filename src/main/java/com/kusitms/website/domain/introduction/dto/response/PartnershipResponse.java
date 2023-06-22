package com.kusitms.website.domain.introduction.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema
public class PartnershipResponse {
    @JsonProperty("logo_url")
    @Schema(description = "파트너사 로고 사진", example = "https://kusitms-bucket.s3.ap-northeast-2.amazonaws.com/intro/partnership/파트너사1.jpg")
    private String logoUrl;
}
