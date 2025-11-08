package com.kusitms.website.domain.blog.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kusitms.website.domain.blog.entity.BlogAuthor;
import com.kusitms.website.domain.blog.entity.BlogPost;
import com.kusitms.website.domain.blog.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogResponse {
    @JsonProperty("blog_post_id")
    @Schema(description = "블로그 글 아이디")
    private Long id;
    @Schema(description = "블로그 글 제목")
    private String title;
    @Schema(description = "블로그 카테고리 이름")
    private Category category;
    @Schema(description = "블로그 주소")
    private String address;
    @Schema(description = "블로그 이미지 주소")
    private String imageAddress;
    @Schema(description = "블로그 글 내용")
    private String content;

    public static BlogResponse fromEntity(BlogPost post) {
        return BlogResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .category(post.getCategory())
                .address(post.getAddress())
                .imageAddress(post.getImageAddress())
                .content(post.getContent())
                .build();
    }


}
