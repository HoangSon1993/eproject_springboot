package com.sontung.eproject_springboot.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComboDetail {
    @EmbeddedId
    ComboDetailId comboDetailId;
    Integer quantity;
    BigDecimal uniquePrice;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    Product product;

    @ManyToOne
    @MapsId("comboId")
    @JoinColumn(name = "combo_id", insertable = false, updatable = false)
    Combo combo;
}
