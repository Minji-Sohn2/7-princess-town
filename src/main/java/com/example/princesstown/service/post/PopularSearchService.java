package com.example.princesstown.service.post;

import com.example.princesstown.entity.PopularSearch;
import com.example.princesstown.entity.SearchHistory;
import com.example.princesstown.repository.post.PopularSearchRepository;
import com.example.princesstown.repository.post.SearchHistoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PopularSearchService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final PopularSearchRepository popularSearchRepository;

    public PopularSearchService(SearchHistoryRepository searchHistoryRepository, PopularSearchRepository popularSearchRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
        this.popularSearchRepository = popularSearchRepository;
    }

    public List<PopularSearch> getTop5PopularSearches() {
        return popularSearchRepository.findTop5ByOrderBySearchCountDesc();
    }

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    public void updatePopularSearches() {
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);

        // 정해진 시간마다 데이터 삭제
        searchHistoryRepository.deleteAllBySearchTimeBefore(thirtyMinutesAgo);

        List<SearchHistory> searchHistories = searchHistoryRepository.findAllBySearchTimeAfter(thirtyMinutesAgo);

        Map<String, Long> keywordCounts = new HashMap<>();
        for (SearchHistory searchHistory : searchHistories) {
            keywordCounts.put(searchHistory.getKeyword(), keywordCounts.getOrDefault(searchHistory.getKeyword(), 0L) + 1);
        }

        List<PopularSearch> popularSearches = new ArrayList<>();
        for (Map.Entry<String, Long> entry : keywordCounts.entrySet()) {
            popularSearches.add(new PopularSearch(entry.getKey(), entry.getValue()));
        }

        popularSearchRepository.deleteAll();
        popularSearchRepository.saveAll(popularSearches);
    }
}




