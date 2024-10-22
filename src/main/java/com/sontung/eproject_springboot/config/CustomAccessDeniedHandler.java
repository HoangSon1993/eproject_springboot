package com.sontung.eproject_springboot.config;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @Summary class xử lý lỗi truy cập bị từ chối (Access Denied)
 * @Description: Khi xảy ra lỗi access denied mà người dùng có ROLE_ADMIN thì redirect về '/admin/...'
 **/
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(
            HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        log.info(String.valueOf(request.isUserInRole("ADMIN")));
        if (request.isUserInRole("ADMIN")) {
            log.info("Access denied for ADMIN, redirecting to /admin/order");
            response.sendRedirect("/admin/home");
        } else {
            log.info("Access denied, redirecting to /user/auth/login");
            response.sendRedirect("/user/auth/login");
        }
    }
}
