package com.example.princesstown.repository.post;

import com.example.princesstown.entity.Location;
import com.example.princesstown.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collections;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByModifiedAtDesc();

//     위치 범위 내의 게시물을 찾는 메서드
    List<Post> findByLocationIn(List<Location> locations);

    List<Post> findByLocationLatitudeBetweenAndLocationLongitudeBetween(Double minLat, Double maxLat, Double minLon, Double maxLon);

    // 위치로 게시물을 찾는 메서드
    default List<Post> findByLocation(Location location) {
        return findByLocationIn(Collections.singletonList(location));
    }
}

