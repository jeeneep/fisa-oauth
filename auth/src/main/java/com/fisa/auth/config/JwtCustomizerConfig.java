package com.fisa.auth.config;

import com.fisa.auth.authentication.model.Users;
import com.fisa.auth.authentication.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;

@Configuration
public class JwtCustomizerConfig {

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(UserRepository userRepository) {
        return context -> {
            // Access Token 발급 시에만 커스터마이징 동작
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                String username = context.getPrincipal().getName();

                // 1번 담당자가 구현한 DB_A.users 연동 로직 호출
                Users user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

                // 명세 3.2장: JWT Payload 스펙 반영 [cite: 23, 24, 25]
                context.getClaims()
                        .subject(user.getId())                 // 핵심: sub에는 반드시 UUID(users.id)를 사용 [cite: 25, 26, 28]
                        .claim("username", user.getUsername()) // 표시용 이름 [cite: 25, 28]
                        .claim("email", user.getEmail());     // 이메일 [cite: 25, 28]
            }
        };
    }
}