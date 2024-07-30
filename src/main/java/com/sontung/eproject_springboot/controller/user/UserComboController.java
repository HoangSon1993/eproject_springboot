package com.sontung.eproject_springboot.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/combo")
public class UserComboController {
    @GetMapping("/index")
    public String getCombos(){
        return "/user/combo/index";
    }
}
