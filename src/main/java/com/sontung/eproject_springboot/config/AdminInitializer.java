package com.sontung.eproject_springboot.config;

import java.util.HashSet;
import java.util.Set;

import jakarta.annotation.PostConstruct;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sontung.eproject_springboot.entity.Account;
import com.sontung.eproject_springboot.entity.Role;
import com.sontung.eproject_springboot.repository.IAccountRepository;
import com.sontung.eproject_springboot.repository.IRoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminInitializer {
    private final IAccountRepository accountRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void init() {
        Role adminRole = roleRepository.findByRoleName("ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setRoleName("ADMIN");
            return roleRepository.save(role);
        });

        var admin = "admin";
        if (!accountRepository.existsByUserName(admin)) {
            Account account = new Account();
            account.setUserName(admin);
            account.setPassword(passwordEncoder.encode(admin));
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            account.setRoles(roles);
            account.setPasswordChanged(false);
            accountRepository.save(account);
        }
    }
}
