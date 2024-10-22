package com.sontung.eproject_springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sontung.eproject_springboot.entity.OrderDetail;
import com.sontung.eproject_springboot.entity.OrderDetailId;

@Repository
public interface IOrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
    List<OrderDetail> findByOrderOrderId(String orderId);

    OrderDetail findOrderDetailByOrderOrderId(String orderId);
}
