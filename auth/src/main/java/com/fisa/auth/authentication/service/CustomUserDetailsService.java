package com.fisa.auth.authentication.service;

import com.fisa.auth.authentication.model.Users;
import com.fisa.auth.authentication.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * @author 승준
     * UserDetails안에 들어갈 username, password를 우리가 만든 entity에 맞게 빌더로 생성해서 반환
     * @param username the username identifying the user whose data is required.
     * @return Users.user
     * @throws UsernameNotFoundException 가 던져짐
     */
    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        return User.builder()
                .username(user.getUsername())
                .password((user.getPassword()))
                .roles(user.getRole().replace("ROLE_", ""))
                .disabled(!user.isEnabled()) // Users.user 에 isEnabled 타입은 Boolean, 해당 값은 회원 탈퇴 여부를 파악할 수 있음.
                .build();
    }
}
