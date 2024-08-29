package com.sontung.eproject_springboot.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Date;
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
    Date orderDate;
    BigDecimal totalAmount;
    String status;
    String shippingAddress;
    String shippingPhone;

    @OneToOne(mappedBy = "order")
    Invoice invoice; // Optional, for reverse navigation

    @ManyToOne
    @JoinColumn(name = "account_id")
    Account account;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    List<OrderDetail> orderDetails;
}
