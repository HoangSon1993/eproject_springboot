package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.entity.Product;
import com.sontung.eproject_springboot.repository.IProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService implements IProductService {
    private final IProductRepository productRepository;

    public ProductService(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> findById(String id) {
        return productRepository.findById(id);
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteById(String id) {
        productRepository.deleteById(id);
    }

    public Page<Product> findByStatusAndProductNameContaining(int status, String productName, Pageable pageable) {
        return productRepository.findByStatusAndProductNameContaining(status,productName,pageable);
    }

    public Page<Product> findByStatusAndCategory_CategoryIdAndProductNameContaining(int status, String categoryId, String search, Pageable pageable) {
        return productRepository.findByStatusAndCategory_CategoryIdAndProductNameContaining(status,categoryId,search,pageable);
    }

    public int countByCategory(String categoryId) {
        return productRepository.countByCategoryId(categoryId);
    }
}
