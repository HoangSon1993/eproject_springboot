package com.sontung.eproject_springboot.controller.user;

import com.sontung.eproject_springboot.entity.Cart;
import com.sontung.eproject_springboot.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return "/user/cart/index";
    }
    @PostMapping("/create")
    public String createCart(@RequestParam String comboId){
        cartService.createCart(comboId);
        return "redirect:/cart/index";
    }
}
