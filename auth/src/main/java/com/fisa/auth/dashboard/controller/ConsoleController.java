package com.fisa.auth.dashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConsoleController {
    @GetMapping("/console")
    public String consoleHome() {
        return "developer/console";
    }
}
