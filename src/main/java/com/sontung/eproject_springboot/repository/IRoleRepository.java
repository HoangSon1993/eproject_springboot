package com.sontung.eproject_springboot.repository;

import com.sontung.eproject_springboot.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByRoleName(String roleName);
}
