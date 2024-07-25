package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IProductService {
    Page<Product> findAll(Pageable pageable);
    List<Product> findAll();
    Optional<Product> findById(String id);
    Product save(Product product);
    void deleteById(String id);
}
