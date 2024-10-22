package com.sontung.eproject_springboot.config;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.sontung.eproject_springboot.entity.Account;
import com.sontung.eproject_springboot.repository.IAccountRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Tính năng này sẽ được phát triển sau.
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final IAccountRepository accountRepository;
    private SavedRequestAwareAuthenticationSuccessHandler savedRequestHandler =
            new SavedRequestAwareAuthenticationSuccessHandler();

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Account account = accountRepository
                .findByUserNameOrEmail(userDetails.getUsername(), userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException());
        boolean isAdmin =
                account.getRoles().stream().anyMatch(role -> role.getRoleName().equals("ROLE_ADMIN"));
        log.info(String.valueOf(isAdmin));
        if (isAdmin && !account.isPasswordChanged()) { // Nếu là Admin và chưa thay đổi mật khẩu
            // Redirect admin to change password page if password is not changed
            getRedirectStrategy().sendRedirect(request, response, "/admin/auth/admin-change-password");
        } else {
            // Use SavedRequestAwareAuthenticationSuccessHandler to handle the saved request
            savedRequestHandler.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
