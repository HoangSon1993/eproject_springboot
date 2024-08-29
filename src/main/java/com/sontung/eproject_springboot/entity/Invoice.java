package com.sontung.eproject_springboot.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String invoiceId; // invoiceNumber
    LocalDateTime invoiceDate; // ngay hoa don duoc lap
    BigDecimal totalAmount; // tong tien
    String paymentStatus; // da thanh toan, chua thanh toan, huy bo
    String paymentMethod; // chuyen khoan, tin dung, tien mat
    String paymentDate; // ngay thanh toan

    @OneToOne
    @JoinColumn(name = "order_id", unique = true) // unique to enforce the one-to-one relationship
    Order order;
}
