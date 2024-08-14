package com.sontung.eproject_springboot.dto;

import com.sontung.eproject_springboot.entity.ComboDetail;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartDetailDTO {
    // todo: Thêm field chứa cart id
    String id;  // id của product hoặc combo mà thôi
    String name; // name chung có thể là product hoặc combo
    String image;
    BigDecimal price;
    int quantity;
    List<ComboDetail> comboDetails;
}
