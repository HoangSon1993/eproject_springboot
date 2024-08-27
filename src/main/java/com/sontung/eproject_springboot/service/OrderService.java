package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.repository.IOrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final IOrderRepository orderRepository;

    public OrderService(IOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
}