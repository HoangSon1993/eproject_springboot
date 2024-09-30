package com.sontung.eproject_springboot.config;

import com.sontung.eproject_springboot.entity.Account;
import com.sontung.eproject_springboot.repository.IAccountRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private IAccountRepository accountRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Account account = accountRepository.findByUserNameOrEmail(userDetails.getUsername(),userDetails.getUsername()).orElseThrow(()-> new RuntimeException());
        boolean isAdmin = account.getRoles().stream().anyMatch(role -> role.getRoleName().equals("ROLE_ADMIN"));
        System.out.println(isAdmin);
        if(isAdmin && !account.isPasswordChanged()){
            getRedirectStrategy().sendRedirect(request, response, "/admin/auth/admin-change-password");

        }else{
            super.onAuthenticationSuccess(request, response, authentication);
        }

    }
}
