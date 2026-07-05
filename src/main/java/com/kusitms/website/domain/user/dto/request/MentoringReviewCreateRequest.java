package com.kusitms.website.domain.user.dto.request;

import com.kusitms.website.domain.mentoring.entity.RecommendationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "멘토링 후기 작성 요청")
public class MentoringReviewCreateRequest {

    @NotNull
    @Schema(description = "멘토링 신청 ID", required = true)
    private Long applicationId;

    @NotEmpty
    @Size(max = 3)
    @Schema(description = "선택한 키워드 ID 목록", required = true)
    private List<Long> keywordIds;

    @Size(max = 1000)
    @Schema(description = "자유 후기", maxLength = 1000)
    private String content;

    @NotNull
    @Schema(description = "추천 여부", required = true)
    private RecommendationType recommendationType;
}
