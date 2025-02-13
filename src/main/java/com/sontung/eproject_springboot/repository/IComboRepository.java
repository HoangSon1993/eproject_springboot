package com.sontung.eproject_springboot.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sontung.eproject_springboot.entity.Combo;

@Repository
public interface IComboRepository extends JpaRepository<Combo, String> {
    @Query("SELECT DISTINCT c " + "FROM Combo c "
            + "JOIN c.comboDetails cd "
            + "JOIN cd.product p "
            + "JOIN p.category cat "
            + "WHERE c.status = 2 AND cat.categoryId = :categoryId")
    List<Combo> findCombosByStatusAndCategory(@Param("categoryId") String categoryId);

    @Query("SELECT c FROM Combo c WHERE c.status = :status")
    Page<Combo> findByStatus(@Param("status") int status, Pageable pageable);

    /**
     * get only Combo_FinalAmount
     * **/
    @Query("select c.finalAmount from Combo c where c.comboId = :comboId")
    BigDecimal getFinalAmountByComboId(@Param("comboId") String comboId);

    // Count combo for user
    @Query("SELECT COUNT(c) FROM Combo c WHERE c.status = :status")
    long countByStatus(@Param("status") int status);

    // Count combo for admin
    @Query("SELECT COUNT(c) FROM Combo c")
    long countCombo();

    Page<Combo> findByStatus(Pageable pageable, int status);
}
