package com.kusitms.website.domain.introduction.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Schema
public class OBLectureRequest {
    @JsonProperty("name")
    @Schema(description = "강연자 이름", example = "김오비")
    private String name;

    @JsonProperty("image_file")
    @Schema(description = "강연자 프로필 사진 파일")
    private MultipartFile imageFile;

    @JsonProperty("topic")
    @Schema(description = "강연 주제", example = "큐시즘 바로 알기")
    private String topic;
}
