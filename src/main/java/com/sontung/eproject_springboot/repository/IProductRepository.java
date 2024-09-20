package com.sontung.eproject_springboot.repository;

import com.sontung.eproject_springboot.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<Product, String> {
    Page<Product> findByStatusAndProductNameContaining(int status, String productName, Pageable pageable);
    Page<Product> findByStatusAndCategory_CategoryIdAndProductNameContaining(int status, String categoryId, String productName, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.categoryId = :categoryId")
    int countByCategoryId(@Param("categoryId") String categoryId);

    /**
     * get only Product_ProductPrice
     * **/
    @Query("select c.price from Product c where c.productId = :productId")
    BigDecimal getPriceByProductId(@Param("productId") String productId);

    Page<Product> findByStatus(Pageable pageable,int status);
}
