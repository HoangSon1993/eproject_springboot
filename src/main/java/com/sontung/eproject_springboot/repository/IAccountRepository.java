package com.sontung.eproject_springboot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sontung.eproject_springboot.entity.Account;

@Repository
public interface IAccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByUserName(String userName);

    Boolean existsByEmail(String email);

    Optional<Account> findByUserNameOrEmail(String userName, String email);

    boolean existsByUserName(String userName);
}
