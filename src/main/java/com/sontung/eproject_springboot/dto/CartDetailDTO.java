package com.sontung.eproject_springboot.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.sontung.eproject_springboot.entity.ComboDetail;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartDetailDTO {
    String id; // id của Cart
    String name; // name chung có thể là product hoặc combo
    String image;
    BigDecimal price; // price one product/combo
    BigDecimal currentPrice; // Giá hiện tại từ product/combo
    int quantity;

    @Builder.Default
    List<ComboDetail> comboDetails = new ArrayList<>();

    boolean checked;

    public void addComboDetail(ComboDetail comboDetail) {
        comboDetails.add(comboDetail);
    }

    public void remobeComboDetail(ComboDetail comboDetail) {
        comboDetails.remove(comboDetail);
    }
}
