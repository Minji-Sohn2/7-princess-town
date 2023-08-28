package com.example.princesstown.repository.user;

import com.example.princesstown.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.Optional;

@RepositoryDefinition(domainClass = User.class, idClass = Long.class)
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryQuery  {

    Optional<User> findByUsername (String username);

    Optional<User> findByEmail(String email);
}
