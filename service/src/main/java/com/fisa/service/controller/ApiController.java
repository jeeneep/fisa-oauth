package com.fisa.service.controller;

import com.fisa.service.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public Map<String, Object> getProfile(@AuthenticationPrincipal Jwt jwt) {

        // 1. Auth 서버 엔티티 기준에 맞춰 토큰에서 정보 추출
        String authServerUserId = jwt.getSubject(); // 보통 Auth 서버의 UUID(id) 또는 username이 들어옵니다.
        String email = jwt.getClaimAsString("email"); // Auth 서버의 email
        String username = jwt.getClaimAsString("username"); // Auth 서버의 username! (우리 DB의 nickname으로 쓸 값)

        // 2. 서비스 DB에 유저 정보 동기화 (우리 DB 필드명인 nickname 자리에 username 값을 넣음)
        userService.syncUserFromAuthServer(authServerUserId, email, username);

        return Map.of(
                "status", "success",
                "message", "리소스 서버 접근 성공 및 유저 동기화 완료!",
                "subject", authServerUserId,
                "email", email != null ? email : "정보 없음",
                "username", username != null ? username : "정보 없음",
                "scopes", jwt.getClaimAsStringList("scope")
        );
    }

    @GetMapping("/posts")
    @PreAuthorize("hasAuthority('SCOPE_read:posts')")
    public Map<String, Object> getPosts() {
        return Map.of("posts", "게시글 목록 데이터");
    }

    @PostMapping("/posts")
    @PreAuthorize("hasAuthority('SCOPE_write:posts')") // Role 조건 삭제!
    public Map<String, Object> createPost() {
        return Map.of("status", "success", "message", "게시글이 작성되었습니다.");
    }

    @GetMapping("/data")
    @PreAuthorize("hasAuthority('SCOPE_read:data')") // read:data 권한이 있어야 통과
    public Map<String, Object> getData() {
        return Map.of("data", "보안이 중요한 비즈니스 데이터");
    }
}
