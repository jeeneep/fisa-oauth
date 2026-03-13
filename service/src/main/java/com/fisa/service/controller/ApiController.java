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

        String authServerUserId = jwt.getSubject(); // Auth 서버의 UUID
        String email = jwt.getClaimAsString("email");
        String username = jwt.getClaimAsString("username");

        // 서비스 DB에 유저 정보 동기화
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
    @PreAuthorize("hasAuthority('SCOPE_write:posts')")
    public Map<String, Object> createPost() {
        return Map.of("status", "success", "message", "게시글이 작성되었습니다.");
    }

    @GetMapping("/data")
    @PreAuthorize("hasAuthority('SCOPE_read:data')")
    public Map<String, Object> getData() {
        return Map.of("data", "보안이 중요한 비즈니스 데이터");
    }
}
