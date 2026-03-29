package com.pawpawlog.user.repository;

import com.pawpawlog.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByUsername(String username);

  boolean existsByNicknameAndTag(String nickname, String tag);
}