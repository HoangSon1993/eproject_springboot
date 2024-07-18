package com.sontung.eproject_springboot.repository;

import com.sontung.eproject_springboot.entity.Combo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IComboRepository extends JpaRepository<Combo, String> {
}
