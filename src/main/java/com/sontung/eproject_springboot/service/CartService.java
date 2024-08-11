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
    @Autowired
    private ProductRepository productRepository;

    @Value("${user.id}")
    private String userId;


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
    public int addComboToCart(String comboId, int quantity) {
        try {
            Combo combo = iComboRepository.findById(comboId).orElseThrow(() -> new RuntimeException("Combo không tồn tại"));
            Account account = iAccountRepository.findById(userId).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
            Cart cart = iCartRepository.findByAccountAndComboId(account, comboId);
            if (cart == null) {
                cart.setComboId(comboId);
                cart.setQuantity(quantity);
                cart.setAccount(account);
                cart.setAmount(combo.getFinalAmount());
                iCartRepository.save(cart);
            } else {
                int newQuantity = cart.getQuantity() + 1;
                cart.setQuantity(newQuantity);
                iCartRepository.save(cart);
            }
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public List<CartDetailDTO> getCarts() {
        List<Cart> carts = iCartRepository.getCartsByAccount_AccountId(userId);
        List<CartDetailDTO> cartDetailDTOList = new ArrayList<>();
        for (Cart cart : carts) {
            if (cart.getComboId() != null) {
                Combo combo = iComboRepository.findById(cart.getComboId()).orElseThrow(() -> new RuntimeException("Combo khônng tồn tại"));
                List<ComboDetail> comboDetailList = iComboDetailRepository.findAll().stream().filter(cd -> cd.getCombo() == combo).toList();
                CartDetailDTO cartDetailDTO = new CartDetailDTO();
                cartDetailDTO.setComboName(combo.getComboName());
                cartDetailDTO.setPrice(combo.getFinalAmount());
                cartDetailDTO.setQuantity(cart.getQuantity());
                cartDetailDTO.setImage(combo.getImage());
                cartDetailDTO.setComboDetails(comboDetailList);
                cartDetailDTOList.add(cartDetailDTO);
            } else {
                if (cart.getProduct() != null) {
                    CartDetailDTO cartDetailDTO = new CartDetailDTO();
                    Product product = cart.getProduct();
                    cartDetailDTO.setComboName(product.getProductName());
                    cartDetailDTO.setPrice(product.getPrice());
                    cartDetailDTO.setQuantity(cart.getQuantity());
                    cartDetailDTO.setImage(product.getImage());
                    cartDetailDTOList.add(cartDetailDTO);
                }
            }
        }
        return cartDetailDTOList;
    }

    //==Tính tổng giá trị của giỏ hàng
    public BigDecimal totalAmount() {
        List<Cart> carts = iCartRepository.getCartsByAccount_AccountId(userId);
        BigDecimal total = BigDecimal.ZERO;
        for (Cart cart : carts) {
            total = total.add(cart.getAmount().multiply(BigDecimal.valueOf(cart.getQuantity())));
        }
        return total;
    }

    //==Tính tổng số lượng sản phẩm trong giỏ hàng
    public int totalItem() {
        List<Cart> carts = iCartRepository.getCartsByAccount_AccountId(userId);
        return carts.size();
    }

}
