package com.example.princesstown.repository.post;

import com.example.princesstown.entity.PopularSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PopularSearchRepository extends JpaRepository<PopularSearch, Long> {
    List<PopularSearch> findTop5ByOrderBySearchCountDesc();
}
