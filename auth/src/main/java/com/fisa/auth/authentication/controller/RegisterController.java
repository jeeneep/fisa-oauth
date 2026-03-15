package com.fisa.auth.authentication.controller;

import com.fisa.auth.authentication.model.User;
import com.fisa.auth.authentication.service.CustomUserDetailsService;
import com.fisa.auth.authentication.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

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
    private final CustomUserDetailsService customUserDetailsService;


    @GetMapping("/register")
    public String registerForm(HttpServletRequest request, HttpServletResponse response, Model model) {
        // 절대 세션을 초기화(invalidate)하면 안 됩니다.

        // 세션에 보관되어 있던 원래 목적지(인가 요청 주소)를 꺼냅니다.
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);

        if (savedRequest != null) {
            // 목적지가 존재하면 HTML 폼에 숨겨두기 위해 Model에 담습니다.
            model.addAttribute("continueUrl", savedRequest.getRedirectUrl());
        }

        return "register";
    }
    // @PostMapping("/register")  // 단일 서버 테스트
    // public String register(@RequestParam String username,
    //                        @RequestParam String password,
    //                        @RequestParam String email,
    //                        Model model) { // addAttribute 를 위한 객체
    //     try {
    //         userService.register(username, password, email);
    //
    //         // return "redirect:/oauth2/authorization/test-client";  // 단일 서버 테스트
    //         return "redirect:/login";  // 통합 서버 테스트
    //
    //     } catch(IllegalArgumentException e) {
    //         model.addAttribute("error", e.getMessage());
    //         return "register";
    //     }
    // }

    @PostMapping("/register")  // 통합 테스트
    public String processRegister(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("email") String email,
            @RequestParam(value = "continueUrl", required = false) String continueUrl,
            HttpServletRequest request,
            HttpServletResponse response) {

        System.out.println(username + " " + password + " " + email);

        // 1. DB에 회원 정보 저장
        userService.register(username, password, email);

        // 2. 강제 자동 로그인 처리
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 인증 정보를 실제 세션에 강제 저장
        HttpSessionSecurityContextRepository repository = new HttpSessionSecurityContextRepository();
        repository.saveContext(SecurityContextHolder.getContext(), request, response);

        // 3. 리다이렉트 처리
        if (continueUrl != null && !continueUrl.isEmpty()) {
            return "redirect:" + continueUrl;
        }

        return "redirect:/";
    }
}