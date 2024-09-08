package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {
    List<OrderDetail> getOrderDetails(String orderId);

    List<OrderDetail> getOrderDetails(String orderId, String comboId);
}
