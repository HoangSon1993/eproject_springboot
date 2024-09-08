package com.sontung.eproject_springboot.service.impl;

import com.sontung.eproject_springboot.entity.Product;
import com.sontung.eproject_springboot.repository.IProductRepository;
import com.sontung.eproject_springboot.repository.SearchRepository;
import com.sontung.eproject_springboot.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final IProductRepository productRepository;
    private final SearchRepository searchRepository;

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

    @Override
    public void getAllProductWithSortByColumnAndSearch(int pageNo, int pageSize, String search, String sortBy) {
        searchRepository.getAllProductWithSortByColoumAndSearch(pageNo, pageSize, search, sortBy);
    }

    @Override
    public Page<Product> findByStatusAndProductNameContaining(int status, String productName, Pageable pageable) {
        return productRepository.findByStatusAndProductNameContaining(status,productName,pageable);
    }
    @Override
    public Page<Product> findByStatusAndCategory_CategoryIdAndProductNameContaining(int status, String categoryId, String search, Pageable pageable) {
        return productRepository.findByStatusAndCategory_CategoryIdAndProductNameContaining(status,categoryId,search,pageable);
    }

    @Override
    public int countByCategory(String categoryId) {
        return productRepository.countByCategoryId(categoryId);
    }
}
