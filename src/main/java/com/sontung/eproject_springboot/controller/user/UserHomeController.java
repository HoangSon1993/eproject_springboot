package com.sontung.eproject_springboot.controller.user;

import com.sontung.eproject_springboot.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/home-page")
@RequiredArgsConstructor
public class UserHomeController {
    @Autowired
    AccountService accountService;
    @GetMapping("")
    public String homePage(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("loggedUserName", authentication.getName());
        return "user/home";
    }
}