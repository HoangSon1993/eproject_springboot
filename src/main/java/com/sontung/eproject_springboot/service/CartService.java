package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.dto.CartDetailDTO;
import com.sontung.eproject_springboot.entity.Cart;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    Cart addProductToCart(String userId, String productId, int quantity);

    // 07/08/2024 Tạo giỏ hàng cho combo(do Tùng làm)
    Cart addComboToCart(String userId, String comboId, int quantity);

    List<CartDetailDTO> getCarts(String userId, List<String> cartItems);

    BigDecimal getTotalAmount(List<CartDetailDTO> cartDetailDTOList);

    int getTotalItem(String userId);

    void removeItemFromCart(String cartId);

    void removeCartItems(List<String> cartItems);

    boolean updateQuantity(String cartId, Integer newQuantity);

    List<CartDetailDTO> getCartByIds(String userId, List<String> cartItems);

}
