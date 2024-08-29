package com.sontung.eproject_springboot.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String invoiceId;
    LocalDate invoiceDate;
    BigDecimal totalAmount;
    String paymentStatus;
    String paymentMethod;

    @OneToOne
    @JoinColumn(name = "order_id", unique = true) // unique to enforce the one-to-one relationship
    Order order;
}
