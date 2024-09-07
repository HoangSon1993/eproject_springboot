package com.sontung.eproject_springboot.controller.user;

import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("UserAuthenticationController")
@RequestMapping("/auth")
public class AuthenticationController {
    @GetMapping("/login")
    public String login(){
        return "/user/authentication/login";
    }
}
