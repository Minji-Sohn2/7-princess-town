package com.example.princesstown.controller.post;

import com.example.princesstown.entity.PopularSearch;
import com.example.princesstown.service.post.PopularSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api")
public class PopularSearchController {

    private final PopularSearchService popularSearchService;

    public PopularSearchController(PopularSearchService popularSearchService) {
        this.popularSearchService = popularSearchService;
    }

    @GetMapping("/popular-searches")
    public ResponseEntity<List<PopularSearch>> getPopularSearches() {
        List<PopularSearch> popularSearches = popularSearchService.getTop5PopularSearches();
        return ResponseEntity.ok(popularSearches);
    }
}