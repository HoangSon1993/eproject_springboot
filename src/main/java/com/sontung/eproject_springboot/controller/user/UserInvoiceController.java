package com.sontung.eproject_springboot.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/invoice")
public class UserInvoiceController {
    @GetMapping("/index")
    public String getInvoice(){
        return "/user/order/index";
    }
}
