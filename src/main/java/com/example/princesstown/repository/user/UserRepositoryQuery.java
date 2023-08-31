package com.example.princesstown.repository.user;

import com.example.princesstown.dto.search.UserSearchCond;
import com.example.princesstown.entity.User;

import java.util.List;

public interface UserRepositoryQuery {
    /**
     * keyword 가 username 에 들어간 user 찾기
     *
     * @param cond keyword (조건)
     * @return 해당하는 user list
     */
    List<User> searchUserByKeyword(UserSearchCond cond);

    /**
     * keyword 가 nickname 에 들어간 user 찾기
     *
     * @param cond keyword (조건)
     * @return 해당하는 user list
     */
    List<User> searchNickByKeyword(UserSearchCond cond);
}
