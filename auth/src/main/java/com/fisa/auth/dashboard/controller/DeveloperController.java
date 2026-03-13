package com.fisa.auth.dashboard.controller;

import com.fisa.auth.dashboard.model.dto.DeveloperSignupRequest;
import com.fisa.auth.dashboard.service.DeveloperService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/developer")
public class DeveloperController {

    private final DeveloperService developerService;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("developerSignupRequest", new DeveloperSignupRequest());
        return "developer/register";
    }

    @PostMapping("/register")
    public String register(@Valid DeveloperSignupRequest developerSignupRequest,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            return "developer/register";
        }

        try {
            developerService.registerDeveloper(developerSignupRequest);
            return "redirect:/developer/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "developer/register";
        }
    }

    @GetMapping("/login")
    public String loginPage() {
        return "developer/login";
    }
}
