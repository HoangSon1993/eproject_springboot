package com.sontung.eproject_springboot.controller.user;

import com.sontung.eproject_springboot.entity.Combo;
import com.sontung.eproject_springboot.entity.Product;
import com.sontung.eproject_springboot.service.ComboService;
import com.sontung.eproject_springboot.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/home-page")
@RequiredArgsConstructor
public class UserHomeController {
    private final ProductService productService;
    private final ComboService comboService;

    @Value("${user.id}")
    private String userId; // userId tạm

    @GetMapping("")
    public String homePage(Model model) {
        // Lấy ra các sản phẩm tiêu biểu
        Page<Product> products = productService.getProductsTypical(0,8);

        // Lấy ra các combo tiêu biểu.
        Page<Combo> combos = comboService.getCombosTypical(0,8);


        model.addAttribute("products", products);
        model.addAttribute("combos", combos);
        return "user/home";
    }


}
