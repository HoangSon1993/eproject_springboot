package com.sontung.eproject_springboot.controller.user;

import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("UserAuthenticationController")
@RequestMapping("/auth")
public class AuthenticationController {
    @GetMapping("/login")
    public String login(){
        return "/user/authentication/login";
    }
    @PostMapping("/login")
    public String authenticateUser() {
        return "redirect:/home-page";
    }
}
