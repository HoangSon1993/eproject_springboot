package com.sontung.eproject_springboot.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AuthenticationController {
    @GetMapping("/auth/login")
    public String login() {
        return "/admin/authentication/login";
    }

    @GetMapping("/auth/admin-change-password")
    public String changPassword() {
        return "/admin/authentication/change-password";
    }
}
