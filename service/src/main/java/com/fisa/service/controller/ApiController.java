package com.fisa.service.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController { // 명세서 산출물명 반영!

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public Map<String, Object> getProfile(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "status", "success",
                "message", "리소스 서버 접근 성공! (profile 권한 확인됨)",
                "subject", jwt.getSubject(), // 토큰에서 유저의 고유 식별자 꺼내기
                "scopes", jwt.getClaimAsStringList("scope") // 토큰이 가진 권한(스코프) 목록 확인
        );
    }

    @GetMapping("/data")
    @PreAuthorize("hasAuthority('SCOPE_read:data')") // read:data 권한이 있어야 통과
    public Map<String, Object> getData() {
        return Map.of("data", "보안이 중요한 비즈니스 데이터");
    }
}