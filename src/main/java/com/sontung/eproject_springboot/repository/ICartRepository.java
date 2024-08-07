package com.sontung.eproject_springboot.repository;

import com.sontung.eproject_springboot.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ICartRepository extends JpaRepository<Cart, String> {
}
