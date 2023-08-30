package com.example.princesstown.repository.location;

import com.example.princesstown.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

//    Location findByLocationIdAndLatitudeAndLongitude(Long id, Double latitude, Double longitude);

    Optional<Location> findByUsers_UserId(Long id);

    List<Location> findByLastUpdateBefore(LocalDateTime lastUpdateBefore);
}