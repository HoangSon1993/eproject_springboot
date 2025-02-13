package com.sontung.eproject_springboot.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.sontung.eproject_springboot.enums.OrderStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String orderId;

    LocalDateTime orderDate;
    BigDecimal totalAmount;

    /**
     * Trong DB sẽ lưu giá trị  0,1,2,3
     * ORRERED: 'Đã đặt hàng'
     * PAID: 'Đã thanh toán'
     * CANCELED: 'Huỷ bỏ'
     * PEDING:'Chờ thanh toán'
     * COD: 'Thanh toán khi nhận hàng'
     */
    @Enumerated(EnumType.ORDINAL)
    OrderStatus status;

    @Column(length = 100)
    String firstName;

    @Column(length = 100)
    String lastName;

    @Column(length = 150)
    String email;

    String shippingAddress;

    @Column(length = 20)
    String shippingPhone;

    @Column(length = 8)
    String code;

    @OneToOne(mappedBy = "order")
    Invoice invoice; // Optional, for reverse navigation

    @ManyToOne
    @JoinColumn(name = "account_id")
    Account account;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Builder.Default
    List<OrderDetail> orderDetails = new ArrayList<>();

    // Add widget to add order details to order.
    public void addOrderDetail(OrderDetail orderDetail) {
        orderDetails.add(orderDetail);
        orderDetail.setOrder(this);
    }

    public void removeOrderDetail(OrderDetail orderDetail) {
        orderDetails.remove(orderDetail);
        orderDetail.setOrder(null);
    }
}
