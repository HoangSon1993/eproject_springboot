package com.sontung.eproject_springboot.controller.user;

import com.sontung.eproject_springboot.dto.request.OrderDtoRequest;
import com.sontung.eproject_springboot.dto.request.PaymentResultDto;
import com.sontung.eproject_springboot.entity.Order;
import com.sontung.eproject_springboot.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/order")
@Controller("UserOrderController")
@SessionAttributes("order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Value("${user.id}")
    private String userId; // userId tạm


    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(@RequestBody OrderDtoRequest orderDtoRequest,
                                                      HttpServletRequest req, HttpServletResponse resp,
                                                      HttpSession session) throws UnsupportedEncodingException {
        Map<String, Object> response = new HashMap<>();
        // 1. Create Order and OrderDetail in Transaction.
        Order order = orderService.createOrder(orderDtoRequest, userId);

        // Luu order vao Session
        session.setAttribute("order", order);

        // 2. Call Vnpay Service to payment.
        String url = "";
        try {
            url = orderService.getVnpay(order, req, resp);
        } catch (UnsupportedEncodingException e) {
            // Todo: handle exception, gui thong bao cho nguoi dung
            throw new RuntimeException(e);
        }
        // 3. Gui phan hoi lai cho Client.
        response.put("success", true);
        response.put("url", url);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/payment-result")
    public String paymentResult(@ModelAttribute PaymentResultDto paymentResultDto, Model model, HttpSession session) {
        if ("00".equals(paymentResultDto.getVnp_ResponseCode())) {
            // Thanh toan thanh cong
            // Tao Invoice va Invoice Detail
            // Lay Order tu trong session
            Order order = (Order) session.getAttribute("order");
            //Todo: Create Invoice and Invoice Detail
//           orderService.creatInvoice(paymentResultDto);

            // Tra ket qua ve cho nguoi dung
        }

        return "/user/order/payment-result";
    }


    @PostMapping("/process-payment")
    public String processPayment(@RequestParam("orderId") String orderId) {
        // Xử lý thanh toán với dịch vụ thanh toán
        // Cập nhật trạng thái đơn hàng
        return "redirect:/order/success";
    }
}