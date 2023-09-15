package com.example.princesstown.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PopularSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;

    private Long searchCount;

    public PopularSearch() {
    }

    public PopularSearch(String keyword, Long searchCount) {
        this.keyword = keyword;
        this.searchCount = searchCount;
    }
}
