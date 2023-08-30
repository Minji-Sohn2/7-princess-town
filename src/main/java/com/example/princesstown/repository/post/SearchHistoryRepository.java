package com.example.princesstown.repository.post;

import com.example.princesstown.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findAllBySearchTimeAfter(LocalDateTime time);

    void deleteAllBySearchTimeBefore(LocalDateTime time);
}
