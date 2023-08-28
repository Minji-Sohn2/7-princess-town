package com.example.princesstown.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.geo.Point;

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
    private Long id;

    @Column
    private Long latitude;
    @Column
    private Long longitude;

    @Column
    private Point point;

    @OneToMany(mappedBy = "location")
    private List<User> Locationlist = new ArrayList<>();
}