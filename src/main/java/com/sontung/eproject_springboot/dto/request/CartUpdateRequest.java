package com.sontung.eproject_springboot.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartUpdateRequest {
    private String id;
    private Integer quantity;
    //    private boolean checked;
}
