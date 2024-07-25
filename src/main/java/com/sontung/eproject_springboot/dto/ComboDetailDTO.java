package com.sontung.eproject_springboot.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
public class ComboDetailDTO {
    String comboId;
    String productId;
    String productName;
    Integer quantity;
    BigDecimal uniquePrice;;
}
