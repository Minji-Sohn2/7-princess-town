package com.example.princesstown.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "location")
@NoArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    @Column
    private Double latitude; // 위도

    @Column
    private Double longitude; // 경도

    @Column
    private Double radius;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate; // 위치 업데이트 시간 기록

    @OneToMany(mappedBy = "location")
    private List<User> users = new ArrayList<>(); // Location과 User 간의 일대다 관계 설정

    @OneToMany(mappedBy = "location")
    private List<Post> locationPosts = new ArrayList<>(); // Location과 Post 간의 일대다 관계 설정
}
