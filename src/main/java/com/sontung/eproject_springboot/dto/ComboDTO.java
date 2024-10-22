package com.sontung.eproject_springboot.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
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
