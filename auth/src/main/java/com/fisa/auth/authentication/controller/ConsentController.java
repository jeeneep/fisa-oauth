package com.fisa.auth.authentication.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class ConsentController {
    @GetMapping("/oauth2/consent")
    public String consent(
            @RequestParam("client_id") String clientId,
            @RequestParam("scope") String scope,
            @RequestParam("state") String state,
            Model model) {
        Set<String> scopeSet = new HashSet<>(Arrays.asList(scope.split(" ")));

        model.addAttribute("clientId", clientId);
        model.addAttribute("scope", scope);
        model.addAttribute("state", state);

        return "consent";
    }

    // Spring Authorization Server가 동의 처리를 자동으로 처리해줘서 POST는 안 만들어도됨.

}
