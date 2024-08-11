package com.sontung.eproject_springboot.repository;

import com.sontung.eproject_springboot.entity.Account;
import com.sontung.eproject_springboot.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICartRepository extends JpaRepository<Cart, String> {
    @Query("SELECT c FROM Cart c WHERE c.account.accountId = :accountId")
    List<Cart> getCartsByAccount_AccountId(@Param("accountId") String accountId);
    Cart findByAccountAndComboId(Account account, String comboId);
    Cart findByProduct_ProductIdAndAccount_AccountId(String productId, String userId);

}
