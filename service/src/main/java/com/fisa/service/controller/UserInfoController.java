package com.fisa.service.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserInfoController {
    @GetMapping("/userinfo")
    public Map<String, Object> userinfo(@AuthenticationPrincipal Jwt jwt) {
        // DB에서 추가 정보 조회 (ex. role)
        return Map.of("sub", jwt.getSubject());
    }
}
