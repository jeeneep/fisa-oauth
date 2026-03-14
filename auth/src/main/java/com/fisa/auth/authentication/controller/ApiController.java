package com.fisa.auth.authentication.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

	// 토큰 검증에 통과하면, 스프링 시큐리티가 Jwt 객체를 주입해 줍니다.
	@GetMapping("/user")
	public Map<String, Object> getUserInfo(@AuthenticationPrincipal Jwt jwt) {
		Map<String, Object> response = new HashMap<>();

		// JWT 토큰 안에 들어있는 정보(Claims)를 꺼내서 응답으로 줍니다.
		response.put("userId", jwt.getSubject()); // 보통 유저 ID
		response.put("issuedAt", jwt.getIssuedAt());
		response.put("expiresAt", jwt.getExpiresAt());

		return response; // JSON 형태로 반환됨
	}
}