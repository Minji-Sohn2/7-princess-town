package com.example.princesstown.service.location;

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

@Slf4j(topic = "LocationService")
@RequiredArgsConstructor
@Service

public class LocationService {

    private final LocationRepository locationRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    //  주어진 위도와 경도를 기준으로 반경 내에 있는 게시물을 찾아 반환하는 메서드
//    public ResponseEntity<ApiResponseDto> getPostsInRadius(Long id, PostByLocationRequestDto requestDto) {
//        // 요청에서 위도와 경도, 반경 정보 가져오기
//        Double latitude = requestDto.getLatitude();
//        Double longitude = requestDto.getLongitude();
//        Double radius = // 해당 유저가 설정한 radius 필요
//
//        // 사용자의 위치 정보 조회
//        Location userLocation = locationRepository.findByLocationIdAndLatitudeAndLongitude(id, latitude, longitude, radius); //여기서 반경 정보를 추가하고
//
//        // 지구 반지름(킬로미터)
//        Double earthRadius = 6371.01;
//
//        // 반경에 따른 위도와 경도 차이 계산
//        Double latDiff = Math.toDegrees(radius / earthRadius);
//        Double lonDiff = Math.toDegrees(radius / (earthRadius * Math.cos(Math.toRadians(latitude))));
//
//        // 최소, 최대 위도와 경도 계산
//        Double minLat = userLocation.getLatitude() - latDiff;
//        Double maxLat = userLocation.getLatitude() + latDiff;
//        Double minLon = userLocation.getLongitude() - lonDiff;
//        Double maxLon = userLocation.getLongitude() + lonDiff;
//
//        // 주어진 범위 내의 게시물 조회
//        List<Post> postsInRadius = postRepository.findByLocationLatitudeBetweenAndLocationLongitudeBetween(minLat, maxLat, minLon, maxLon);
//
//        // 게시물이 없는 경우
//        if (postsInRadius.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto(HttpStatus.NOT_FOUND.value(), "반경 내에 사용자가 존재하지 않습니다."));
//        }
//
//        // 게시물 저장
//        postRepository.saveAll(postsInRadius);
//
//        // 게시물 DTO 변환
//        List<PostResponseDto> postInRadiusResponseDto = postsInRadius.stream()
//                .map(PostResponseDto::new)
//                .collect(Collectors.toList());
//
//        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto(HttpStatus.OK.value(), "게시물 조회 성공", postInRadiusResponseDto));
//    }



    // 위치를 업데이트 하는 메서드
    @Transactional
    public void updateLocationAndRelatedEntities(Long id, Double latitude, Double longitude, Double radius) {
        LocalDateTime now = LocalDateTime.now();

        Optional<Location> optionalLocation = locationRepository.findByUsers_UserId(id);

        Location location = optionalLocation.orElse(new Location()); // Create a new location if not found
        if (optionalLocation.isPresent()) {
            location = optionalLocation.get(); // Use the location if it's present
        }

        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setRadius(radius);
        location.setLastUpdate(now);

        Location updatedLocation = locationRepository.save(location);

        // Update the user's location
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setLocation(updatedLocation);
            userRepository.save(user);
        }

        // Update the posts associated with the user's location
        List<Post> postsInLocation = postRepository.findByLocation(updatedLocation);
        for (Post post : postsInLocation) {
            post.setLocation(updatedLocation);
            postRepository.save(post);
        }
    }


    // 한달 후 위치를 소멸시키는 메서드(레디스로 관리하기엔 데이터량이 많고 기간이 김)
    public void deleteExpiredLocations() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<Location> locationsToDelete = locationRepository.findByLastUpdateBefore(oneMonthAgo);
        locationRepository.deleteAll(locationsToDelete);
    }

    @Scheduled(cron = "0 0 * * * *") // 매 분마다 실행
    public void scheduledTasks() {
        deleteExpiredLocations();
    }
}
