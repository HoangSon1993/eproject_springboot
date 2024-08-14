package com.sontung.eproject_springboot.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
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
