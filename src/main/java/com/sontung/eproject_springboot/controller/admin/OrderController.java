package com.sontung.eproject_springboot.controller.admin;

import com.sontung.eproject_springboot.entity.Order;
import com.sontung.eproject_springboot.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
@RequestMapping("/admin/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @Value("${aws.s3.bucket.url}")
    String s3BucketUrl;

    @ModelAttribute("s3BucketUrl")
    public String s3BucketUrl() {
        return s3BucketUrl;
    }
    @GetMapping
    public String getOrders(@RequestParam(required = false, defaultValue = "0") int amongPrice,
                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date filterDate,
                            @RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "9") int size,
                            Model model) {
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        if (amongPrice == 0 && filterDate==null) {
            Page<Order> orderList = orderService.getOrders(page, size);
            int totalPages = (int) (Math.ceil((double) orderService.countOrder() / size));
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("orders", orderList);
        } else if(amongPrice == 0){
            Page<Order> orderList = orderService.getOrdersByFilterDateOrder(filterDate, page, size);
            model.addAttribute("filterDate", filterDate);
            model.addAttribute("orders", orderList);
            int totalPages = (int) (Math.ceil((double) orderService.countOrderByFilterDate(filterDate) / size));
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("orders", orderList);
        } else {
            if(filterDate==null){
                model.addAttribute("orders", orderService.getOrdersByPrice(amongPrice, page, size));
                int totalPages = (int) (Math.ceil((double) orderService.countOrderByPrice(amongPrice) / size));
                model.addAttribute("totalPages", totalPages);
                model.addAttribute("amongPrice", amongPrice);
            }
            else{
                model.addAttribute("filterDate", filterDate);
                model.addAttribute("amongPrice", amongPrice);
                long totalItems =  orderService.countOrderByPriceAndFilterDate(amongPrice, filterDate);
                int totalPages = (int) (Math.ceil((double)  totalItems/ size));
                model.addAttribute("totalPages", totalPages);
                model.addAttribute("orders", orderService.getOrdersByPriceAndDate(amongPrice, filterDate, page, size));
            }
        }
        return "/admin/order/index";
    }
    @GetMapping("/detail")
    public String getOrderDetails(@RequestParam String orderId, Model model){
        model.addAttribute("order", orderService.getOrder(orderId));
        model.addAttribute("orderDetails", orderService.getOrderDetails(orderId));
//        model.addAttribute("combos", orderService.getCombosOrder(orderId));
        return "/admin/order/detail";
    }
}
