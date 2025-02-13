package com.sontung.eproject_springboot.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.sontung.eproject_springboot.dto.RegisterDTO;
import com.sontung.eproject_springboot.dto.UpdatedAccountDTO;
import com.sontung.eproject_springboot.service.AccountService;

import lombok.RequiredArgsConstructor;

@Controller("userAuthenticationController")
@RequestMapping("/user")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AccountService accountService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/auth/login")
    public String login() {
        return "/user/authentication/login";
    }

    @PostMapping("/auth/login")
    public String authenticateUser() {
        return "redirect:/home-page";
    }

    @GetMapping("/register")
    public String register() {
        return "/user/authentication/register";
    }

    @PostMapping("/registerConfirm")
    public String registerConfirm(@ModelAttribute RegisterDTO registerDTO, Model model) {
        var result = accountService.createAccount(registerDTO);
        if (result == 1) {
            // accountService.autoLogin(registerDTO.getUserName(), registerDTO.getPassword(), authenticationManager);
            model.addAttribute("successMessage", "Đăng ký thành công và đã đăng nhập");
            return "redirect:/user/auth/login";
        } else {
            String errorMessage = accountService.getErrorMessage(result);
            model.addAttribute("errorMessage", errorMessage);
            return "redirect:/user/register";
        }
    }

    @GetMapping("/checkUsername")
    public ResponseEntity<Map<String, Boolean>> checkUserNameExists(@RequestParam("username") String username) {
        boolean exists = accountService.exitUsername(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-info")
    public String getMyInfo(@RequestParam String username, Model model) {
        model.addAttribute("user", accountService.findByUserNameOrEmail(username));
        return "/user/authentication/my-info";
    }

    @GetMapping("/edit-info")
    public String editInfo(@RequestParam String username, Model model) {
        model.addAttribute("user", accountService.findByUserNameOrEmail(username));
        return "/user/authentication/edit-info";
    }

    @PostMapping("/edit-info-confirm")
    public String editInfoConfirm(@ModelAttribute UpdatedAccountDTO account, Model model) {
        model.addAttribute("user", accountService.updateAccount(account));
        return "redirect:/user/my-info?username=" + account.getUserName();
    }

    @GetMapping("/change-password")
    public String changePassword() {
        return "/user/authentication/change-password";
    }

    @PostMapping("/change-password-confirm")
    public String changePasswordConfirm(String password, String newPassword, String newPasswordConfirm) {
        accountService.changePassword(password, newPassword, newPasswordConfirm);
        return "/user/authentication/change-password";
    }
}
