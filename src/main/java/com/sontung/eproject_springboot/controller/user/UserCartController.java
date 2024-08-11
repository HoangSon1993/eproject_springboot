package com.sontung.eproject_springboot.controller.user;

import com.sontung.eproject_springboot.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class UserCartController {
    @Autowired
    CartService cartService;

    @Value("${aws.s3.bucket.url}")
    String s3BucketUrl;

    @ModelAttribute("s3BucketUrl")
    public String s3BucketUrl() {
        return s3BucketUrl;
    }

    @GetMapping("/index")
    public String getCarts(Model model){
        model.addAttribute("cartDetail", cartService.getCarts());
        model.addAttribute("amount", cartService.totalAmount());
        return "/user/cart/index";
    }
    @PostMapping("/create")
    public String createCart(@RequestParam String comboId, @RequestParam int quantity){
        cartService.createCart(comboId, quantity);
        return "redirect:/cart/index";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(@RequestParam("productId") String productId,
                                                         @RequestParam("quantity") int quantity) {
        Map<String, Object> response = new HashMap<>();
        // TODO: 9/8/24 Xử lý thêm vào giỏ hàng
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
}
