package com.sontung.eproject_springboot.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailId implements Serializable {
    @Column(name = "product_id")
    String productId;

    @Column(name = "order_id")
    String orderId;
}
