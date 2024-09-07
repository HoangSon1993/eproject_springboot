package com.sontung.eproject_springboot.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/auth")
public class AuthenticationController {
    @GetMapping("/login")
    public String login(){
        return "/admin/authentication/login";
    }

    @GetMapping("/home")
    public String home(){
        return "/admin/home";
    }
}
