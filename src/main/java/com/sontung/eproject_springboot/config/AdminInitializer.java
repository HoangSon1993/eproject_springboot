package com.sontung.eproject_springboot.config;

import com.sontung.eproject_springboot.entity.Account;
import com.sontung.eproject_springboot.entity.Role;
import com.sontung.eproject_springboot.repository.IAccountRepository;
import com.sontung.eproject_springboot.repository.IRoleRepository;
import com.sontung.eproject_springboot.service.AccountService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class AdminInitializer {
    @Autowired
    private IAccountRepository accountRepository;
    @Autowired
    IRoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @PostConstruct
    @Transactional
    public void init(){
        Role adminRole = roleRepository.findByRoleName("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setRoleName("ADMIN");
                    return roleRepository.save(role);
                });

        if(!accountRepository.existsByUserName("admin")){
            Account account = new Account();
            account.setUserName("admin");
            account.setPassword(passwordEncoder.encode("admin"));
            account.setFullName("Mrs.Luyen");
            account.setPhone("0985220947");
            account.setEmail("admin@gmail.com");
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            account.setRoles(roles);
            account.setPasswordChanged(false);
            accountRepository.save(account);
        }
    }
}
