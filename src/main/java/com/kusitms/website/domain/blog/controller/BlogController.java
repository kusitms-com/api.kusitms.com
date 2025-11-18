package com.kusitms.website.domain.blog.controller;

import com.kusitms.website.domain.blog.dto.response.BlogResponse;
import com.kusitms.website.domain.blog.entity.BlogPost;
import com.kusitms.website.domain.blog.entity.Category;
import com.kusitms.website.domain.blog.entity.Position;
//import com.kusitms.website.domain.blog.service.BlogService;
import com.kusitms.website.domain.blog.service.BlogService;
import com.kusitms.website.domain.project.dto.response.MeetupDetailResponse;
import com.kusitms.website.domain.project.dto.response.MeetupResponse;
import com.kusitms.website.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
@Tag(name = "Blog", description = "블로그 API Document")
public class BlogController {

    private final BlogService blogService;

    @GetMapping("")
    @Operation(summary = "블로그 리스트 조회", description = "기수, 파트, 카테고리 조건으로 블로그 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = MeetupResponse.class))),
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = MeetupDetailResponse.class)))
    })
    public ResponseEntity<BaseResponse> getBlogList(
            @RequestParam(required = false) Integer generation,
            @RequestParam(required = false) Position position,
            @RequestParam(required = false) Category category,
            Pageable pageable
    ) {
        Page<BlogResponse> result = blogService.getFilteredPostsWithPaging(generation, position, category, pageable);
        return ResponseEntity.ok(new BaseResponse(result));
    }
}
