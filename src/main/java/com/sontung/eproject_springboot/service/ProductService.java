package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Page<Product> findAll(Pageable pageable);
    List<Product> findAll();
    Optional<Product> findById(String id);
    Product save(Product product);
    void deleteById(String id);

    void getAllProductWithSortByColumnAndSearch(int pageNo, int pageSize, String search, String sort);

    Page<Product> findByStatusAndProductNameContaining(int status, String productName, Pageable pageable);

    Page<Product> findByStatusAndCategory_CategoryIdAndProductNameContaining(int status, String categoryId, String search, Pageable pageable);

    int countByCategory(String categoryId);
}
