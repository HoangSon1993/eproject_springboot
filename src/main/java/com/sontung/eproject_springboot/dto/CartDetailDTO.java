package com.sontung.eproject_springboot.dto;

import com.sontung.eproject_springboot.entity.ComboDetail;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartDetailDTO {
    String comboName;
    String image;
    BigDecimal price;
    int quantity;
    List<ComboDetail> comboDetails;
}
