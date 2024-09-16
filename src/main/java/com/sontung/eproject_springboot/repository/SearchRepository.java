package com.sontung.eproject_springboot.repository;

import com.sontung.eproject_springboot.entity.Order;
import org.springframework.data.domain.Page;

public interface SearchRepository {
    void getAllProductWithSortByColoumAndSearch(int pageNo, int pageSize, String search, String sortBy);

    Page<Order> getAllOrderWithSortByColoumAndSearch(int pageNo, int pageSize, String search, String sortBy, String status, String accountId);

    Page<Order> getAllOrderWithSortByColoumAndSearchCriteriaBuider(int pageNo, int pageSize, String search, String status, String sortBy, String accountId);
}
