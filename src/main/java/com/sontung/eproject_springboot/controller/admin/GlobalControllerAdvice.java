package com.sontung.eproject_springboot.controller.admin;

import com.sontung.eproject_springboot.entity.Account;
import com.sontung.eproject_springboot.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {
    @Autowired
    AccountService accountService;
    @ModelAttribute("loggedInUser")
    public Account addUserToModel(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)){
            String username = authentication.getName();
            Account account = accountService.findByUserNameOrEmail(username);
            return account;
            //return username;
        }
        return null;
    }
}
