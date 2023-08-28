package com.example.princesstown.entity;

import com.example.princesstown.dto.request.BoardRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "boards")
public class Board extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_userId", nullable = false)
    private User user;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Post> postList = new ArrayList<>();

    public Board(BoardRequestDto boardRequestDto, User user){
        this.user = user;
        this.title = boardRequestDto.getTitle();
    }

    public void update(BoardRequestDto boardRequestDto){
        this.title = boardRequestDto.getTitle();
    }


}
