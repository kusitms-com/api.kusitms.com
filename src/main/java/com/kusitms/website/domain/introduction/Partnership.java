package com.kusitms.website.domain.introduction;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Partnership {
    @Id
    @Column(name = "partner_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnerId;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "introduction_id")
    private Introduction introduction;

    @Builder
    public Partnership(Introduction introduction, String imageUrl) {
        this.introduction = introduction;
        introduction.getPartnership().add(this);
        this.imageUrl = imageUrl;
    }
}
