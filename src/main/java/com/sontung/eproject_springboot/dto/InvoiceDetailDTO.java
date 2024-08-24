package com.sontung.eproject_springboot.dto;

import com.sontung.eproject_springboot.entity.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class InvoiceDetailDTO {
    Invoice invoice;
    Product product;
    Combo combo;
    Integer quantity;
    BigDecimal uniquePrice;
    int comboQuantity;
    //List<ComboDetail> comboDetails;
}
