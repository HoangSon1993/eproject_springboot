package com.sontung.eproject_springboot.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home-page")
public class UserHomeController {
    @GetMapping("")
    public String homePage(){
        return "/user/home";
    }
}
