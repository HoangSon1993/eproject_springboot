package com.sontung.eproject_springboot.service.impl;

import com.sontung.eproject_springboot.dto.RegisterDTO;
import com.sontung.eproject_springboot.entity.Account;
import com.sontung.eproject_springboot.repository.IAccountRepository;
import com.sontung.eproject_springboot.service.AccountService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{
    private PasswordEncoder passwordEncoder;
    @Autowired
    IAccountRepository accountRepository;
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Account account = accountRepository.findByUserNameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not exits by Username or Email"));
        Set<GrantedAuthority> authorities = account.getRoles().stream()
                .map((role -> new SimpleGrantedAuthority("ROLE_"+role.getRoleName())))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                usernameOrEmail,
                account.getPassword(),
                authorities
        );
    }

    @Override
    public Account findByUserNameOrEmail(String username) throws UsernameNotFoundException{
        return accountRepository.findByUserNameOrEmail(username, username)
                .orElseThrow(()-> new UsernameNotFoundException("User not exits"));
    }

    @Override
    public int createAccount(RegisterDTO registerDTO) {
        if(!registerDTO.getPassword().equals(registerDTO.getPasswordConfirm())){
            return 0;
        }
        else{
            if(accountRepository.existsByUserName(registerDTO.getUserName())){
                return 0;
            }
            else{
                if(accountRepository.existsByEmail(registerDTO.getEmail())){
                    return 0;
                }
                else{
                    Account account = new Account();
                    account.setUserName(registerDTO.getUserName());
                    account.setPhone(registerDTO.getPhone());
                    account.setFullName(registerDTO.getFullName());
                    account.setEmail(registerDTO.getEmail());
                    account.setDob(registerDTO.getDob());
                    account.setAvatar(registerDTO.getAvatar());
                    account.setAddress(registerDTO.getAddress());
                    account.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
                    accountRepository.save(account);
                }
            }
        }
        return 1;
    }
}
