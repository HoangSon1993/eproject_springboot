package com.sontung.eproject_springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sontung.eproject_springboot.entity.Category;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, String> {}
