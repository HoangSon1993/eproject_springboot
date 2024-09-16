package com.sontung.eproject_springboot.config;

import com.sontung.eproject_springboot.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    private final
    CartService cartService;

    @Value("${user.id}")
    private String userId; // userId tạm

    /**
     * @Summary: Đếm số item có trong Cart.
     * @Description: Dùng để hiển thị số lượng item ở icon Cart.
     **/
    @ModelAttribute("cartItemCount")
    public int getCartItemCount() {
        // Lấy ra user_id (sau khi đã áp dung Spring Security)
        // Hiện tại đang dùng User Tạm
        if(userId != null) {
            return cartService.getTotalItem(userId);
        }
        return 0;
    }
}