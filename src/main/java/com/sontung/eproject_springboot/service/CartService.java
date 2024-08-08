package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.dto.CartDetailDTO;
import com.sontung.eproject_springboot.entity.Account;
import com.sontung.eproject_springboot.entity.Cart;
import com.sontung.eproject_springboot.entity.Combo;
import com.sontung.eproject_springboot.entity.ComboDetail;
import com.sontung.eproject_springboot.repository.IAccountRepository;
import com.sontung.eproject_springboot.repository.ICartRepository;
import com.sontung.eproject_springboot.repository.IComboDetailRepository;
import com.sontung.eproject_springboot.repository.IComboRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    // TODO: 07/08/2024 Tạo giỏ hàng cho product(do anh Sơn làm)

    // TODO: 07/08/2024 Tạo giỏ hàng cho combo(do Tùng làm)
    public int createCart(String comboId){
        Combo combo = iComboRepository.findById(comboId).orElseThrow(() -> new RuntimeException());
        Account account = iAccountRepository.findById(userId).orElseThrow(() -> new RuntimeException());
        Cart cart = new Cart();
        cart.setComboId(comboId);
        cart.setQuantity(1);
        cart.setAccount(account);
        cart.setAmount(combo.getFinalAmount());
        iCartRepository.save(cart);
        return 1;
    }

    public List<CartDetailDTO> getCarts(){
        List<Cart> carts = iCartRepository.getCartsByAccount_AccountId(userId);
        List<CartDetailDTO> cartDetailDTOList = new ArrayList<>();
        for (Cart cart: carts) {
            Combo combo = iComboRepository.findById(cart.getComboId()).orElseThrow(() -> new RuntimeException());
            List<ComboDetail> comboDetailList = iComboDetailRepository.findAll().stream().filter(cd -> cd.getCombo()==combo).toList();
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
}
