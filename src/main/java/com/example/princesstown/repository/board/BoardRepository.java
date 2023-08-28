package com.example.princesstown.repository.board;

import com.example.princesstown.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findAllByOrderByCreatedAtDesc();
}
