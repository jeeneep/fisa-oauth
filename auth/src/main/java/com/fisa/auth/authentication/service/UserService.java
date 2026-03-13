package com.fisa.auth.authentication.service;

import com.fisa.auth.authentication.model.Users;
import com.fisa.auth.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(String username, String password, String email){
        if(userRepository.existsByUsername(username)){
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        if(userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        Users user = Users.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .role("ROLE_USER")
                .enabled(true)
                .build();

        userRepository.save(user);
    }
}
