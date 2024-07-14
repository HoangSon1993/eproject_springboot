package com.sontung.eproject_springboot.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComboDetail {
    @EmbeddedId
    ComboDetailId id;
    Integer quantity;
    BigDecimal uniquePrice;

    @ManyToOne
    @MapsId("product_id")
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    Product product;

    @ManyToOne
    @MapsId("combo_id")
    @JoinColumn(name = "combo_id", insertable = false, updatable = false)
    Combo combo;
}
