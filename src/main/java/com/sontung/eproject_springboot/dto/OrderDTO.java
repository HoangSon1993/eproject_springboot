package com.sontung.eproject_springboot.dto;

import com.sontung.eproject_springboot.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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