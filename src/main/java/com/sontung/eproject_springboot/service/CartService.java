package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.dto.CartDetailDTO;
import com.sontung.eproject_springboot.entity.*;
import com.sontung.eproject_springboot.exception.ProductNotFoundException;
import com.sontung.eproject_springboot.exception.UserNotFoundException;
import com.sontung.eproject_springboot.repository.IAccountRepository;
import com.sontung.eproject_springboot.repository.ICartRepository;
import com.sontung.eproject_springboot.repository.IComboDetailRepository;
import com.sontung.eproject_springboot.repository.IComboRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {
    @Autowired
    ICartRepository iCartRepository;
    @Autowired
    IComboRepository iComboRepository;
    @Autowired
    IAccountRepository iAccountRepository;
    @Autowired
    IComboDetailRepository iComboDetailRepository;
    @Value("${user.id}")
    private String userId;
    @Autowired
    private ProductRepository productRepository;

    // TODO: 07/08/2024 Tạo giỏ hàng cho product(do anh Sơn làm)
    public void addProductToCart(String productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Sản phẩm không tồn tại"));
        Account account = iAccountRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Người dùng không tồn tại"));
        Cart cartItem = iCartRepository.findByProduct_ProductIdAndAccount_AccountId(productId, userId);
        if (cartItem != null) {
            // Update quantity and amount
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setAmount(cartItem.getAmount().add(product.getPrice().multiply(BigDecimal.valueOf(quantity))));
        } else {
            // The product does not exist in the cart
            cartItem = new Cart();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setAmount(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            cartItem.setAccount(account);
        }
        // Save change to database
        iCartRepository.save(cartItem);
    }

    // TODO: 07/08/2024 Tạo giỏ hàng cho combo(do Tùng làm)
    public int createCart(String comboId) {
        Combo combo = iComboRepository.findById(comboId).orElseThrow(() -> new RuntimeException("Combo không tồn tại"));
        Account account = iAccountRepository.findById(userId).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        Cart cart = new Cart();
        cart.setComboId(comboId);
        cart.setQuantity(1);
        cart.setAccount(account);
        cart.setAmount(combo.getFinalAmount());
        iCartRepository.save(cart);
        return 1;
    }

    public List<CartDetailDTO> getCarts() {
        List<Cart> carts = iCartRepository.getCartsByAccount_AccountId(userId);
        List<CartDetailDTO> cartDetailDTOList = new ArrayList<>();
        for (Cart cart : carts) {
            Combo combo = iComboRepository.findById(cart.getComboId()).orElseThrow(() -> new RuntimeException());
            List<ComboDetail> comboDetailList = iComboDetailRepository.findAll().stream().filter(cd -> cd.getCombo() == combo).toList();
            CartDetailDTO cartDetailDTO = new CartDetailDTO();
            cartDetailDTO.setComboName(combo.getComboName());
            cartDetailDTO.setPrice(combo.getFinalAmount());
            cartDetailDTO.setQuantity(cart.getQuantity());
            cartDetailDTO.setImage(combo.getImage());
            cartDetailDTO.setComboDetails(comboDetailList);
            cartDetailDTOList.add(cartDetailDTO);
        }
        return cartDetailDTOList;
    }

    public List<Cart> getCartByAccountId() {
        return iCartRepository.findCartByAccount_AccountId(userId);
    }
}
