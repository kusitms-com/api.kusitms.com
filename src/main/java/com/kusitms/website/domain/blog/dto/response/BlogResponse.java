package com.kusitms.website.domain.blog.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kusitms.website.domain.blog.entity.BlogAuthor;
import com.kusitms.website.domain.blog.entity.BlogPost;
import com.kusitms.website.domain.blog.entity.Category;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;

@Getter
@Builder
@NoArgsConstructor
public class BlogResponse {
    @JsonProperty("blog_post_id")
    @Schema(description = "블로그 글 아이디")
    private Long id;
    @Schema(description = "블로그 글 제목")
    private String title;
    @Schema(description = "블로그 주소")
    private String address;
    @Schema(description = "블로그 이미지 주소")
    private String imageAddress;
    @Schema(description = "블로그 글 내용")
    private String content;
    @Schema(description = "활동명")
    private String categoryName;
    @Schema(description = "기수")
    private Integer cardinal;
    @Schema(description = "파트명")
    private String position;

    @QueryProjection
    public BlogResponse(Long id,
                        String title,
                        String address,
                        String imageAddress,
                        String content,
                        String categoryName,
                        Integer cardinal,
                        String position) {
        this.id = id;
        this.title = title;
        this.address = address;
        this.imageAddress = imageAddress;
        this.content = content;
        this.categoryName = categoryName;
        this.cardinal = cardinal;
        this.position = position;
    }




}
