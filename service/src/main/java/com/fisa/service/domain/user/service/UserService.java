package com.fisa.service.domain.user.service;

import com.fisa.service.domain.user.entity.User;
import com.fisa.service.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User syncUserFromAuthServer(String authServerUserId, String email, String nickname) {
        return userRepository.findByAuthServerUserId(authServerUserId)
                .map(existingUser -> {
                    existingUser.updateProfile(email, nickname);
                    return existingUser;
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .authServerUserId(authServerUserId)
                            .email(email)
                            .nickname(nickname)
                            .build();
                    return userRepository.save(newUser);
                });
    }
}