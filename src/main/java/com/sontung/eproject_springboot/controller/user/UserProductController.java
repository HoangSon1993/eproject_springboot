package com.sontung.eproject_springboot.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("product")
public class UserProductController {
    @GetMapping("/index")
    public String getProducts(){
        return "/user/product/index";
    }
}
