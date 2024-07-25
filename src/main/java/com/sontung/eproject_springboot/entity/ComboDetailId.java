package com.sontung.eproject_springboot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComboDetailId implements Serializable {
    @Column(name = "combo_id")
    String comboId;
    @Column(name = "product_id")
    String productId;

    public String getComboId() {
        return comboId;
    }

    public String getProductId() {
        return productId;
    }

    public void setComboId(String comboId) {
        this.comboId = comboId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
