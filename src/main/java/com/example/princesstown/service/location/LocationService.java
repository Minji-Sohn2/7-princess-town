package com.example.princesstown.service.location;

import com.example.princesstown.dto.request.PostByLocationRequestDto;
import com.example.princesstown.dto.response.PostResponseDto;
import com.example.princesstown.entity.Location;
import com.example.princesstown.entity.Post;
import com.example.princesstown.entity.User;
import com.example.princesstown.repository.location.LocationRepository;
import com.example.princesstown.repository.post.PostRepository;
import com.example.princesstown.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j(topic = "LocationService")
@RequiredArgsConstructor
@Service

public class LocationService {

    private final LocationRepository locationRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    //  주어진 위도와 경도를 기준으로 반경 내에 있는 게시물을 찾아 반환하는 메서드
//    public List<PostResponseDto> getPostsInRadius(Long id, PostByLocationRequestDto requestDto) {
//        Double latitude = requestDto.getLatitude();
//        Double longitude = requestDto.getLongitude();
//        Double radius = requestDto.getRadius();;
//        Location userLocation = locationRepository.findByLocationIdAndLatitudeAndLongitude(id, latitude, longitude);
//
//        log.error("데이터 제대로 받아오지 못함");
//
//        Double earthRadius = 6371.01;
//
//        Double latDiff = Math.toDegrees(radius / earthRadius);
//        Double lonDiff = Math.toDegrees(radius / (earthRadius * Math.cos(Math.toRadians(latitude))));
//
//        Double minLat = userLocation.getLatitude() - latDiff;
//        Double maxLat = userLocation.getLatitude() + latDiff;
//        Double minLon = userLocation.getLongitude() - lonDiff;
//        Double maxLon = userLocation.getLongitude() + lonDiff;
//
////            List<Post> postsInRadius = postRepository.findByLocationLatitudeBetweenAndLocationLongitudeBetween(minLat, maxLat, minLon, maxLon);
//
//        // 명시적으로 Location 정보 설정 후 저장
////        for (Post post : postsInRadius) {
////            post.setLocation(userLocation);
////        }
////        postRepository.saveAll(postsInRadius);
//
////        List<PostResponseDto> postInRadiusResponseDto = postsInRadius.stream()
////                .map(PostResponseDto::new)
////                .collect(Collectors.toList());
////
////        return postInRadiusResponseDto;
//    }

    // 위치를 업데이트 하는 메서드
    @Transactional
    public void updateLocationAndRelatedEntities(Long id, Double latitude, Double longitude) {
        LocalDateTime now = LocalDateTime.now();

//        Optional<Location> optionalLocation = locationRepository.findByUsers_UserId(id);

//        Location location = optionalLocation.orElse(new Location()); // Create a new location if not found
//        if (optionalLocation.isPresent()) {
//            location = optionalLocation.get(); // Use the location if it's present
//        }

//        location.setLatitude(latitude);
//        location.setLongitude(longitude);
//        location.setLastUpdate(now);
//
//        Location updatedLocation = locationRepository.save(location);

        // Update the user's location
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
//            user.setLocation(updatedLocation);
            userRepository.save(user);
        }

        // Update the posts associated with the user's location
//        List<Post> postsInLocation = postRepository.findByLocation(updatedLocation);
//        for (Post post : postsInLocation) {
//            post.setLocation(updatedLocation);
//            postRepository.save(post);
//        }
    }


    // 한달 후 위치를 소멸시키는 메서드
    public void deleteExpiredLocations() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<Location> locationsToDelete = locationRepository.findByLastUpdateBefore(oneMonthAgo);
        locationRepository.deleteAll(locationsToDelete);
    }

    @Scheduled(cron = "0 * * * * *") // 매 분마다 실행
    public void scheduledTasks() {
        deleteExpiredLocations();
    }
}
