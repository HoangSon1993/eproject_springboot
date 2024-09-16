package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.dto.RegisterDTO;
import com.sontung.eproject_springboot.entity.Account;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
@Service
public interface AccountService extends UserDetailsService {
    @Override
    UserDetails loadUserByUsername(String usernameOrEmail);

    Account findByUserNameOrEmail(String username);

    int createAccount(RegisterDTO registerDTO);
}
