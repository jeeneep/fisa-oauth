package com.fisa.service.domain.user.repository;

import com.fisa.service.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Auth Server의 ID로 서비스 DB의 User를 찾기 위한 메서드
    Optional<User> findByAuthServerUserId(String authServerUserId);
}