package com.sontung.eproject_springboot.entity;

import com.sontung.eproject_springboot.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    LocalDate orderDate;
    BigDecimal totalAmount;

    /**
     * Trong DB sẽ lưu giá trị  0,1,2
     * PEDING:'Chờ thanh toán'
     * PAID: 'Đã thanh toán'
     * CANCELED: 'Huỷ bỏ'
     */
    @Enumerated(EnumType.ORDINAL)
    OrderStatus status;
    String shippingAddress;
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
