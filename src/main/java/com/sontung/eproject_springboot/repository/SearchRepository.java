package com.sontung.eproject_springboot.repository;

import com.sontung.eproject_springboot.entity.Order;
import com.sontung.eproject_springboot.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface SearchRepository {
    void getAllProductWithSortByColoumAndSearch(int pageNo, int pageSize, String search, String sortBy);

    Page<Order> getAllOrderWithSortByColoumAndSearch(int pageNo, int pageSize, String search, String sortBy, String status, String accountId);

    Page<Product> getAllProductWithSortByColumAndSearch(int pageNo, int pageSize, String search, int amongPrice, String status, String sortBy, LocalDate timeStart, LocalDate timeEnd);

    Page<Order> getAllOrderWithSortByColoumAndSearchCriteriaBuider(int pageNo, int pageSize, String search, String status, String sortBy, String accountId);

    Page<Order> getAllOrderWithFilterDateAmongPriceAndSearchCriteriaBuider(int pageNo, int pageSize, String search, int amongPrice, String status, LocalDateTime filterDate, LocalDateTime filterDate2);

    Page<Product> findByStatusAndProductNameContainingAndPriceFilter(int status, String categoryId, String search, int amongPrice, Pageable pageable);
}
