package com.example.princesstown.repository.user;

import com.example.princesstown.dto.search.SimpleUserInfoDto;
import com.example.princesstown.dto.search.UserSearchCond;

import java.util.List;

public interface UserRepositoryQuery {
    /**
     * keyword 가 username 이나 nickname 에 들어간 user 찾기
     *
     * @param cond keyword (조건)
     * @return 해당하는 user list
     */
    List<SimpleUserInfoDto> search(UserSearchCond cond);
}
