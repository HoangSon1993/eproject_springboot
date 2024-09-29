package com.sontung.eproject_springboot.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Summary class xử lý lỗi truy cập bị từ chối (Access Denied)
 * @Description: Khi xảy ra lỗi access denied mà người dùng có ROLE_ADMIN thì redirect về '/admin/...'
 **/
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        System.out.println(request.isUserInRole("ADMIN"));
        if (request.isUserInRole("ADMIN")) {
            System.out.println("Access denied for ADMIN, redirecting to /admin/order");
            response.sendRedirect("/admin/home");
        } else {
            System.out.println("Access denied, redirecting to /user/auth/login");
            response.sendRedirect("/user/auth/login");
        }
    }
}
