package com.sontung.eproject_springboot.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.sontung.eproject_springboot.dto.RegisterDTO;
import com.sontung.eproject_springboot.dto.UpdatedAccountDTO;
import com.sontung.eproject_springboot.entity.Account;

@Service
public interface AccountService extends UserDetailsService {
    @Override
    UserDetails loadUserByUsername(String usernameOrEmail);

    Account findByUserNameOrEmail(String username);

    int createAccount(RegisterDTO registerDTO);

    boolean exitUsername(String username);

    void autoLogin(String username, String password, AuthenticationManager authenticationManager);

    // Hàm lấy thông báo lỗi dựa trên mã lỗi
    String getErrorMessage(int result);

    Account updateAccount(UpdatedAccountDTO accountDTO);

    int changePassword(String password, String newPassword, String newPasswordConfirm);
}
