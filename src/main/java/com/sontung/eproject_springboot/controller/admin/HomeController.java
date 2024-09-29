package com.sontung.eproject_springboot.controller.admin;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/home")
public class HomeController {
    @GetMapping("")
    public String adminHome(){
        return "/admin/home";
    }
}
