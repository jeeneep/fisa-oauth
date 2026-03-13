package com.fisa.auth.dashboard.service;

import com.fisa.auth.authentication.model.Users;
import com.fisa.auth.authentication.repository.UserRepository;
import com.fisa.auth.dashboard.model.Developer;
import com.fisa.auth.dashboard.model.dto.DeveloperSignupRequest;
import com.fisa.auth.dashboard.repository.DeveloperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeveloperService {
    private final UserRepository userRepository;
    private final DeveloperRepository developerRepository;
    private final PasswordEncoder passwordEncoder;

    // 개발자 회원가입
    @Transactional
    public void registerDeveloper(DeveloperSignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        Users user = Users.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role("ROLE_DEVELOPER")
                .enabled(true)
                .build();

        userRepository.save(user);

        Developer developer = Developer.builder()
                .user(user)
                .companyName(request.getCompanyName())
                .build();

        developerRepository.save(developer);
    }
}
