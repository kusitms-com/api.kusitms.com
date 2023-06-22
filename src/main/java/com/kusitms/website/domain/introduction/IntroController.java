package com.kusitms.website.domain.introduction;

import com.kusitms.website.domain.introduction.dto.request.IntroRequest;
import com.kusitms.website.domain.introduction.dto.response.*;
import com.kusitms.website.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Introduction", description = "학회 소개 API Document")
public class IntroController {

    private final IntroService introService;

    @GetMapping("/introductions")
    @Operation(summary = "학회 소개 페이지 정보 조회", description = "학회 소개 페이지의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = IntroResponse.class)))
    })
    public ResponseEntity<BaseResponse> getIntroduction() {
        return ResponseEntity.ok(new BaseResponse(introService.getIntroduction()));
    }

    @PutMapping(value = "/admin/introductions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "학회 소개 정보 수정", description = "학회 소개 페이지의 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공")
    })
    public ResponseEntity<BaseResponse> updateIntroduction(@ModelAttribute(name = "introduction_request")IntroRequest request) {
        introService.updateIntroduction();
        return ResponseEntity.ok(new BaseResponse());
    }
}
