package com.example.princesstown.dto.search;

import lombok.Getter;

@Getter
public class UserSearchCond {
    private String keyword;

    public UserSearchCond(String keyword) {
        this.keyword = keyword;
    }
}