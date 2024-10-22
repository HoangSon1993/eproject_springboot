package com.sontung.eproject_springboot.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sontung.eproject_springboot.entity.Product;

public interface ProductService {
    Page<Product> findAll(Pageable pageable);

    List<Product> findAll();

    Optional<Product> findById(String id);

    Product save(Product product);

    void deleteById(String id);

    void getAllProductWithSortByColumnAndSearch(int pageNo, int pageSize, String search, String sort);

    Page<Product> findByStatusAndProductNameContaining(int status, String productName, Pageable pageable);

    Page<Product> findByStatusAndCategory_CategoryIdAndProductNameContaining(
            int status, String categoryId, String search, Pageable pageable);

    Page<Product> getProductsTypical(int pageNumber, int pageSize, String categoryId);

    int countByCategory(String categoryId);
}
