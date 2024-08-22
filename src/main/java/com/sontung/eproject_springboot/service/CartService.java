package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.dto.CartDetailDTO;
import com.sontung.eproject_springboot.entity.*;
import com.sontung.eproject_springboot.exception.ProductNotFoundException;
import com.sontung.eproject_springboot.exception.UserNotFoundException;
import com.sontung.eproject_springboot.repository.IAccountRepository;
import com.sontung.eproject_springboot.repository.ICartRepository;
import com.sontung.eproject_springboot.repository.IComboDetailRepository;
import com.sontung.eproject_springboot.repository.IComboRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final ICartRepository iCartRepository;
    private final IComboRepository iComboRepository;
    private final IAccountRepository iAccountRepository;
    private final IComboDetailRepository iComboDetailRepository;
    private final ProductRepository productRepository;

    @Value("${user.id}")
    private String userId;

    public CartService(
            ICartRepository iCartRepository,
            IComboRepository iComboRepository,
            IAccountRepository iAccountRepository,
            IComboDetailRepository iComboDetailRepository,
            ProductRepository productRepository) {
        this.iCartRepository = iCartRepository;
        this.iComboRepository = iComboRepository;
        this.iAccountRepository = iAccountRepository;
        this.iComboDetailRepository = iComboDetailRepository;
        this.productRepository = productRepository;
    }


    // 07/08/2024 Tạo giỏ hàng cho product(do anh Sơn làm)
    public Cart addProductToCart(String productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Sản phẩm không tồn tại"));
        Account account = iAccountRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Người dùng không tồn tại"));
        Cart cartItem = iCartRepository.findByProduct_ProductIdAndAccount_AccountId(productId, userId);
        if (cartItem != null) {
            // Update quantity and amount
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            // The product does not exist in the cart
            cartItem = new Cart();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setPrice(product.getPrice());
            cartItem.setAccount(account);
        }
        // Save change to database
        iCartRepository.save(cartItem);
        return cartItem;
    }

    // 07/08/2024 Tạo giỏ hàng cho combo(do Tùng làm)
    public Cart addComboToCart(String comboId, int quantity) {
        try {
            Combo combo = iComboRepository.findById(comboId).orElseThrow(() -> new RuntimeException("Combo không tồn tại"));
            Account account = iAccountRepository.findById(userId).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
            Cart cart = iCartRepository.findByAccountAndComboId(account, comboId);
            if (cart == null) {
                Cart new_cart = new Cart();
                new_cart.setComboId(comboId);
                new_cart.setQuantity(quantity);
                new_cart.setAccount(account);
                new_cart.setPrice(combo.getFinalAmount()); // Tung xem lai vi da change amount -> price
                iCartRepository.save(new_cart);
            } else {
                int newQuantity = cart.getQuantity() + quantity;
                cart.setQuantity(newQuantity);
                iCartRepository.save(cart);
            }
            return cart;
        } catch (Exception e) {
            return null;
        }
    }

    public List<CartDetailDTO> getCarts() {
        List<Cart> carts = iCartRepository.getCartsByAccount_AccountId(userId);
        List<CartDetailDTO> cartDetailDTOList = new ArrayList<>();
        for (Cart cart : carts) {
            if (cart.getComboId() != null && cart.getProduct() == null) {
                Combo combo = iComboRepository.findById(cart.getComboId()).orElseThrow(() -> new RuntimeException("Combo khônng tồn tại"));
                List<ComboDetail> comboDetailList = iComboDetailRepository.findAll().stream().filter(cd -> cd.getCombo() == combo).toList();
                CartDetailDTO cartDetailDTO = new CartDetailDTO();

                cartDetailDTO.setId(cart.getCartId());
                cartDetailDTO.setName(combo.getComboName());
                cartDetailDTO.setPrice(combo.getFinalAmount());
                cartDetailDTO.setQuantity(cart.getQuantity());
                cartDetailDTO.setImage(combo.getImage());
                cartDetailDTO.setComboDetails(comboDetailList);

                cartDetailDTOList.add(cartDetailDTO);
            } else {
                // Case CartDetail is Product
                CartDetailDTO cartDetailDTO = new CartDetailDTO();
                Product product = cart.getProduct();
                cartDetailDTO.setId(cart.getCartId());
                cartDetailDTO.setName(product.getProductName());
                cartDetailDTO.setPrice(product.getPrice());
                cartDetailDTO.setQuantity(cart.getQuantity());
                cartDetailDTO.setImage(product.getImage());

                cartDetailDTOList.add(cartDetailDTO);

            }
        }
        return cartDetailDTOList;
    }

    //==Tính tổng giá trị của giỏ hàng
    public BigDecimal getTotalAmount(List<String> checkedItems) {
        List<Cart> carts = iCartRepository.getCartsByAccount_AccountId(userId);
        BigDecimal total = BigDecimal.ZERO;
        for (Cart cart : carts) {
            // Kiểm tra nếu cartItem được check
            if(checkedItems.contains(cart.getCartId())) {
                total = total.add(cart.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            }
        }
        return total;
    }

    //==Tính tổng số lượng sản phẩm trong giỏ hàng
    public int getTotalItem() {
        List<Cart> carts = iCartRepository.getCartsByAccount_AccountId(userId);
        return carts.size();
    }

    public void removeItemFromCart(String cartId) {
        try {
            iCartRepository.deleteById(cartId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xoá sản phẩm ra khỏi giỏ hàng");
        }
    }

    public boolean updateQuantity(String cartId, Integer newQuantity) {
        Cart cart = iCartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại."));
        //BigDecimal amount;

//        if (!cart.getAccount().getAccountId().equals(userId)) {
//            throw new RuntimeException("Người dùng không có quyền cập nhật sản phẩm");
//        }

        if (cart.getComboId() == null) {
            cart.setQuantity(newQuantity);
            cart.setPrice(cart.getProduct().getPrice());
           // amount = cart.getProduct().getPrice().multiply(BigDecimal.valueOf(newQuantity));
        } else {
            // Case Combo
            cart.setQuantity(newQuantity);
            Combo combo = iComboRepository.findById(cart.getComboId()).orElseThrow(() -> new RuntimeException("Combo không tồn tại"));
            cart.setPrice(combo.getFinalAmount());
           // amount = combo.getFinalAmount().multiply(BigDecimal.valueOf(newQuantity));
        }
        iCartRepository.save(cart);
        return true;
    }
}
