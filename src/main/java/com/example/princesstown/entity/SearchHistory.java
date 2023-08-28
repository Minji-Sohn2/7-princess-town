package com.example.princesstown.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
public class SearchHistory extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;

    private LocalDateTime searchTime;

    @PrePersist
    protected void onCreate() {
        searchTime = LocalDateTime.now();
    }

}

