package com.sontung.eproject_springboot.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
@CrossOrigin
public class UserCartController {
    @GetMapping("/index")
    public String getCarts(){
        return "/user/cart/index";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(@RequestParam("productId") String productId,
                                                         @RequestParam("quantity") int quantity) {
        Map<String, Object> response = new HashMap<>();
//        try {
//            // Logic để thêm sản phẩm vào giỏ hàng
//            cartService.addToCart(productId, quantity);
//            response.put("success", true);
//        } catch (Exception e) {
//            response.put("success", false);
//        }
//        return ResponseEntity.ok(response);
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
}
