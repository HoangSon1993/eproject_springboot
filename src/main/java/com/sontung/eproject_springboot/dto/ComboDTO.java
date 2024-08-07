package com.sontung.eproject_springboot.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComboDTO {
    String comboName;
    LocalDate startDate;
    LocalDate endDate;
    String description;
    String productsJson;
    BigDecimal finalAmount;
}
