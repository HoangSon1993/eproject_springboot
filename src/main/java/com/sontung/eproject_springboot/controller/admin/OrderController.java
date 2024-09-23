package com.sontung.eproject_springboot.controller.admin;

import com.sontung.eproject_springboot.entity.Order;
import com.sontung.eproject_springboot.enums.OrderStatus;
import com.sontung.eproject_springboot.repository.SearchRepository;
import com.sontung.eproject_springboot.service.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/order")
public class OrderController {
    private final OrderService orderService;
    private final SearchRepository searchRepository;

    @Value("${aws.s3.bucket.url}")
    String s3BucketUrl;

    public OrderController(OrderService orderService, SearchRepository searchRepository) {
        this.orderService = orderService;
        this.searchRepository = searchRepository;
    }

    @ModelAttribute("s3BucketUrl")
    public String s3BucketUrl() {
        return s3BucketUrl;
    }

    @GetMapping
    public String getOrders(@RequestParam(required = false, defaultValue = "0") int amongPrice,
                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate filterDate,
                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate filterDate2,
                            @RequestParam(defaultValue = "0") int pageNo,
                            @RequestParam(defaultValue = "9") int pageSize,
                            @RequestParam(defaultValue = "") String search,
                            @RequestParam(defaultValue = "") String status,
                            Model model) {
        LocalDateTime timeStart = null;
        LocalDateTime timeEnd = null;

        if (filterDate != null) {
            timeStart = filterDate.atStartOfDay();
            if (filterDate2 != null) {
                timeEnd = LocalDateTime.of(filterDate2, LocalTime.MAX);
            } else {
                timeEnd = LocalDateTime.of(filterDate, LocalTime.MAX);
            }
        }
        Page<Order> orders = searchRepository.getAllOrderWithFilterDateAmongPriceAndSearchCriteriaBuider(
                pageNo,
                pageSize,
                search,
                amongPrice,
                status,
                timeStart,
                timeEnd);

        model.addAttribute("orders", orders);
        model.addAttribute("amongPrice", amongPrice);
        model.addAttribute("filterDate", filterDate != null ? filterDate : "");
        model.addAttribute("filterDate2", filterDate2 != null ? filterDate2 : "");
        model.addAttribute("status", status);
        model.addAttribute("search", search);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("pageNo", pageNo);

        // Lấy danh sách các trạng thái đơn hàng
        Map<String, String> statuses = OrderStatus.toMap();

        model.addAttribute("statuses", statuses);
        return "/admin/order/index";
    }

    @PatchMapping("/confirm-payment-cod/{orderId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> confirmPaymentCod(@PathVariable String orderId) {
        Map<String, String> response = new HashMap<>();
        try{
            orderService.confirmPaymentCOD(orderId);
            response.put("success", "true");
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("success", "false");
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/detail")
    public String getOrderDetails(@RequestParam String orderId, Model model) {
        model.addAttribute("order", orderService.getOrder(orderId));
        model.addAttribute("orderDetails", orderService.getOrderDetails(orderId));
//        model.addAttribute("combos", orderService.getCombosOrder(orderId));
        return "/admin/order/detail";
    }
}
