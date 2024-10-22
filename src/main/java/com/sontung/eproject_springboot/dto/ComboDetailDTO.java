package com.sontung.eproject_springboot.dto;

import java.math.BigDecimal;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComboDetailDTO {
    String comboId;
    String productId;
    String productName;
    Integer quantity;
    BigDecimal uniquePrice;
}
