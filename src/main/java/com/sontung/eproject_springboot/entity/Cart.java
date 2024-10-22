package com.sontung.eproject_springboot.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String cartId;

    Integer quantity;
    BigDecimal price; // change from amount to price

    String productId;
    String comboId;

    @ManyToOne
    @JoinColumn(name = "account_id")
    Account account;
}
