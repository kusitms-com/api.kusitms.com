package com.kusitms.website.domain.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class CurrentCardinal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cardinal;

    public CurrentCardinal(Integer cardinal) {
        this.cardinal = cardinal;
    }

    public void updateCardinal(Integer cardinal) {
        this.cardinal = cardinal;
    }
}
