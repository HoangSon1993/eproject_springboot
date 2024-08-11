package com.sontung.eproject_springboot.controller.user;

import com.sontung.eproject_springboot.entity.Cart;
import com.sontung.eproject_springboot.entity.Combo;
import com.sontung.eproject_springboot.exception.ProductNotFoundException;
import com.sontung.eproject_springboot.exception.UserNotFoundException;
import com.sontung.eproject_springboot.service.CartService;
import com.sontung.eproject_springboot.service.ComboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class UserCartController {
    @Autowired
    CartService cartService;

    @Autowired
    ComboService comboService;

    @Value("${aws.s3.bucket.url}")
    String s3BucketUrl;

    @ModelAttribute("s3BucketUrl")
    public String s3BucketUrl() {
        return s3BucketUrl;
    }

    @GetMapping("/index")
    public String getCarts(Model model) {
        List<Cart> cartItems = cartService.getCartByAccountId();

        Map<String, Combo> comboMap = new HashMap<>();
        for(Cart cart : cartItems) {
            if(cart.getComboId() != null) {
                Combo combo = comboService.getComboById(cart.getComboId());
                comboMap.put("combo_" + cart.getComboId(), combo);
            }
        }
        model.addAttribute("comboMap", comboMap);
        return "/user/cart/index";
    }

    @PostMapping("/create")
    public String createCart(@RequestParam String comboId) {
        cartService.createCart(comboId);
        return "redirect:/cart/index";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addProductToCart(@RequestParam("productId") String productId,
                                                                @RequestParam("quantity") int quantity) {
        Map<String, Object> response = new HashMap<>();
        // TODO: 9/8/24 Xử lý thêm vào giỏ hàng
        try {
            cartService.addProductToCart(productId, quantity);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException | UserNotFoundException ex) {
            response.put("success", false);
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Đã xảy ra lỗi không mong muốn");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
