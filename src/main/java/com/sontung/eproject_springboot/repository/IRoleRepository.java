package com.sontung.eproject_springboot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sontung.eproject_springboot.entity.Role;

@Repository
public interface IRoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByRoleName(String roleName);
}
