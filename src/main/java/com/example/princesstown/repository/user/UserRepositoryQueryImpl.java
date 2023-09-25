package com.example.princesstown.repository.user;

import com.example.princesstown.dto.search.SimpleUserInfoDto;
import com.example.princesstown.dto.search.UserSearchCond;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.jpa.AvailableHints;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

import static com.example.princesstown.entity.QUser.user;

@Component
@RequiredArgsConstructor
public class UserRepositoryQueryImpl implements UserRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<SimpleUserInfoDto> search(UserSearchCond cond) {
        var query = jpaQueryFactory.select(Projections.constructor(SimpleUserInfoDto.class,
                        user.userId, user.username, user.nickname))
                .from(user)
                .where(
                        Objects.requireNonNull(containsKeyword(user.username, cond.getKeyword()))
                                .or(containsKeyword(user.nickname, cond.getKeyword()))
                );
        query.setHint(AvailableHints.HINT_READ_ONLY, true);
        return query.fetch();
    }

    private BooleanExpression containsKeyword(StringPath field, String keyword) {
        return StringUtils.hasText(keyword) ? field.containsIgnoreCase(keyword) : null;
    }
}
