package com.sontung.eproject_springboot.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderForm {
    private String firstName;
    private String lastName;
    private String shippAddress;
    private String shippingPhone;
    private String email;
    private List<String> cartItems;
}