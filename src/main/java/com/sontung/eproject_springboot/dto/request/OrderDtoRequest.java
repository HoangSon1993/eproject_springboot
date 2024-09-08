package com.sontung.eproject_springboot.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDtoRequest {
    @NotBlank(message = "Tên không được để trống.")
    private String firstName;

    @NotBlank(message = "Họ không được để trống.")
    private String lastName;

    @NotBlank(message = "Địa chỉ không được để trống.")
    private String shippAddress;

    // Todo: Kiểm tra Phone hơp lệ với RegExp
    @NotBlank(message = "Số điện thoaị không được để trống.")
    @Pattern(regexp = "^(84|0[3|5|7|8|9])[0-9]{8}$", message = "Số điện thoại không hợp lệ.")
    /**
     * ^(84|0[3|5|7|8|9]): Bắt đầu với 84 (mã quốc gia) hoặc 0 theo sau bởi các đầu số hợp lệ 3, 5, 7, 8, 9.
     * [0-9]{8}$: Theo sau đó là 8 chữ số từ 0 đến 9.
     **/
    private String shippingPhone;

    @Email(message = "Email không hợp lệ.")
    @NotBlank(message = "Email không được để trống.")
    private String email;

    @NotEmpty(message = "Phải chọn ít nhất 1 sản phẩm.")
    private List<String> cartItems;
}