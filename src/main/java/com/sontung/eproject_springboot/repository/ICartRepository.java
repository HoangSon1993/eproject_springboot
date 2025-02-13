package com.sontung.eproject_springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sontung.eproject_springboot.entity.Account;
import com.sontung.eproject_springboot.entity.Cart;

@Repository
public interface ICartRepository extends JpaRepository<Cart, String> {
    @Query("SELECT c FROM Cart c WHERE c.account.accountId = :accountId")
    List<Cart> getCartsByAccount_AccountId(@Param("accountId") String accountId);

    List<Cart> getCartsByCartIdAndAccount_AccountId(
            @Param("cartId") String cartId, @Param("accountId") String accountId);

    Cart findByAccountAndComboId(Account account, String comboId);

    Cart findByProductIdAndAccount_AccountId(String productId, String accountId);

    @Query("SELECT c FROM Cart c WHERE c.account.accountId = :accountId "
            + "AND ((:productId IS NOT NULL AND c.productId = :productId) "
            + "OR (:comboId IS NOT NULL AND c.comboId = :comboId))")
    Cart findByAccountIdAndProductIdOrComboId(
            @Param("accountId") String accountId,
            @Param("productId") String productId,
            @Param("comboId") String comboId);

    Cart findByCartIdAndAccount_AccountId(String cartId, String accountId);

    int countCartsByAccount_AccountId(@Param("accountId") String accountId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.productId = :productId AND c.account.accountId = :accountId")
    void removeProductFromCart(@Param("productId") String productId, @Param("accountId") String accountId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.comboId = :comboId AND c.account.accountId = :accountId")
    void removeComboFromCart(@Param("comboId") String comboId, @Param("accountId") String accountId);
}
