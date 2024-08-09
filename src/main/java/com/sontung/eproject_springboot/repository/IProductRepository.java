package com.sontung.eproject_springboot.repository;

import com.sontung.eproject_springboot.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductRepository extends JpaRepository<Product, String> {
    Page<Product> findByStatusAndProductNameContaining(int status, String productName, Pageable pageable);
    Page<Product> findByStatusAndCategory_CategoryIdAndProductNameContaining(int status, String categoryId, String productName, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.categoryId = :categoryId")
    int countByCategoryId(@Param("categoryId") String categoryId);

}
