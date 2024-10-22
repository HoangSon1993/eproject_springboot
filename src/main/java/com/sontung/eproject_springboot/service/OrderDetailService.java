package com.sontung.eproject_springboot.service;

import java.util.List;

import com.sontung.eproject_springboot.entity.OrderDetail;

public interface OrderDetailService {
    List<OrderDetail> getOrderDetails(String orderId);

    List<OrderDetail> getOrderDetails(String orderId, String comboId);
}
