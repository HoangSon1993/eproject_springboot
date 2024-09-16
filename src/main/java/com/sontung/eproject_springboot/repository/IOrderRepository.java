package com.sontung.eproject_springboot.repository;

import com.sontung.eproject_springboot.dto.OrderDTO;
import com.sontung.eproject_springboot.entity.Order;
import com.sontung.eproject_springboot.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IOrderRepository extends JpaRepository<Order, String> {
    Order findByCodeAndAccount_AccountId(String code, String accountId);

    @Query("SELECT new com.sontung.eproject_springboot.dto.OrderDTO(o.orderId, o.status, o.orderDate) FROM Order o " +
            "WHERE o.status = :status AND o.orderDate < :orderDate")
    List<OrderDTO> findByStatusAndOrderDateBefore(
            @Param("status") OrderStatus status,
            @Param("orderDate") LocalDateTime orderDate);
}
