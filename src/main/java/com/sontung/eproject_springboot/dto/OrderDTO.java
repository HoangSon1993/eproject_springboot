package com.sontung.eproject_springboot.dto;

import java.time.LocalDateTime;

import com.sontung.eproject_springboot.enums.OrderStatus;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderDTO {
    private String orderId;
    private OrderStatus status;
    private LocalDateTime orderDate;

    // Constructor
    public OrderDTO(String orderId, OrderStatus status, LocalDateTime orderDate) {
        this.orderId = orderId;
        this.status = status;
        this.orderDate = orderDate;
    }
}
