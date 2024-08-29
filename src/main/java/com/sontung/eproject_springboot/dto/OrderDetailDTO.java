package com.sontung.eproject_springboot.dto;

import com.sontung.eproject_springboot.entity.Combo;
import com.sontung.eproject_springboot.entity.Order;
import com.sontung.eproject_springboot.entity.Product;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class OrderDetailDTO {
    Order order;
    Product product;
    Combo combo;
    Integer quantity;
    BigDecimal uniquePrice;
    int comboQuantity;
}
