package com.example.princesstown.controller.search;

import com.example.princesstown.dto.search.SearchUserResponseDto;
import com.example.princesstown.dto.search.UserSearchCond;
import com.example.princesstown.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@Slf4j(topic = "SearchController")
@RequiredArgsConstructor
public class SearchController {

    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<SearchUserResponseDto> searchUserByUsername(
            @RequestParam("keyword") String keyword
    ) {
        log.info("사용자 찾기 컨트롤러");
        SearchUserResponseDto result = userService.searchUserByKeyword(new UserSearchCond(keyword));
        return ResponseEntity.ok().body(result);
    }

}
