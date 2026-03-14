package com.fisa.auth.authentication.controller;

import com.fisa.auth.authentication.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * GET  /login                     → 로그인 페이지
 * POST /login                     → 로그인 처리 (Spring Security가 처리)
 * GET  /register                  → 회원가입 페이지
 * POST /register                  → 회원가입 처리
 * GET  /oauth2/consent            → 동의 화면
 * POST /oauth2/consent            → 동의 처리
 */
@Controller
@RequiredArgsConstructor
public class RegisterController {

    private final UserService userService;


    @GetMapping("/register")
    public String registerForm(HttpServletRequest request) {
        // 새로운 가입을 시작할 때 기존 세션을 강제로 날려 이전 유저와의 연결고리를 끊습니다.
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return "register"; // Render "register.html"
    }
    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String email,
                           Model model) { // addAttribute 를 위한 객체
        try {
            userService.register(username, password, email);

            return "redirect:/oauth2/authorization/test-client";
            // return "redirect:/login";

        } catch(IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

//    @GetMapping("/oauth2/consent")
//    @PostMapping("/oauth2/consent")
}
