package com.sontung.eproject_springboot.repository;

import com.sontung.eproject_springboot.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICartRepository extends JpaRepository<Cart, String> {
    List<Cart> getCartsByAccount_AccountId(String accountId);
    List<Cart> findCartByAccount_AccountId(String accountId);
    Cart findByProduct_ProductIdAndAccount_AccountId(String productId, String accountId);
}
