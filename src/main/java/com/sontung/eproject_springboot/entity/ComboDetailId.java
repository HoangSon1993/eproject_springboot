package com.sontung.eproject_springboot.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComboDetailId implements Serializable {
    @Column(name = "combo_id")
    String comboId;

    @Column(name = "product_id")
    String productId;

    public void setComboId(String comboId) {
        this.comboId = comboId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
