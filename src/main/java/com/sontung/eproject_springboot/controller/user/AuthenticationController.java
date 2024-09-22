package com.sontung.eproject_springboot.controller.user;

import com.sontung.eproject_springboot.dto.RegisterDTO;
import com.sontung.eproject_springboot.entity.Account;
import com.sontung.eproject_springboot.service.AccountService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller("UserAuthenticationController")
@RequestMapping("/user")
public class AuthenticationController {
    @Autowired
    AccountService accountService;
    @GetMapping("/auth/login")
    public String login(){
        return "/user/authentication/login";
    }
    @PostMapping("/auth/login")
    public String authenticateUser() {
        return "redirect:/home-page";
    }
    @GetMapping("/register")
    public String register(){
        return "/user/authentication/register";
    }
    @PostMapping("/registerConfirm")
    public String registerConfirm(@ModelAttribute RegisterDTO registerDTO){
        var result = accountService.createAccount(registerDTO);
        if(result==1){
            return "redirect:/user/home";
        }
//        else(){
//            return "redirect:/register";
//        }
        return "/user/home";
    }

    @GetMapping("/checkUsername")
    public ResponseEntity<Map<String, Boolean>> checkUserNameExists(@RequestParam("username") String username){
        boolean exists = accountService.exitUsername(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}
