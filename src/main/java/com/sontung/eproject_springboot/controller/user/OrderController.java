package com.sontung.eproject_springboot.controller.user;

import com.sontung.eproject_springboot.dto.request.OrderDtoRequest;
import com.sontung.eproject_springboot.dto.request.PaymentResultDto;
import com.sontung.eproject_springboot.entity.Order;
import com.sontung.eproject_springboot.repository.IOrderRepository;
import com.sontung.eproject_springboot.service.CartService;
import com.sontung.eproject_springboot.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.out;

@RequestMapping("/order")
@Controller("UserOrderController")
@SessionAttributes("order")
public class OrderController {
    private final OrderService orderService;
    private final HttpSession httpSession;
    private final CartService cartService;
    private final IOrderRepository orderRepository;

    public OrderController(OrderService orderService, HttpSession httpSession, CartService cartService, IOrderRepository orderRepository) {
        this.orderService = orderService;
        this.httpSession = httpSession;
        this.cartService = cartService;
        this.orderRepository = orderRepository;
    }

    @Value("${user.id}")
    private String userId; // userId tạm

    /**
     * @Summary: Create Order and Order_detail
     * @Description: Sử lý thanh toán ở page cart/checkout sau khi người dùng kiểm tra đơn hàng,
     * và người dùng bấm nút thanh toán.
     **/
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(@RequestBody OrderDtoRequest orderDtoRequest,
                                                      HttpServletRequest req, HttpServletResponse resp,
                                                      HttpSession session) throws UnsupportedEncodingException {
        Map<String, Object> response = new HashMap<>();
        // 1. Create Order and OrderDetail in Transaction.
        Order order = orderService.createOrder(orderDtoRequest, userId);

        // 2. Xoá cartItems from cart and Session.
        List<String> checkedItems = (List<String>) session.getAttribute("checkedItems");
        session.removeAttribute("checkedItems");

        cartService.removeCartItems(checkedItems);
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

    /**
     * @Summary: Nhận kết quả từ VnPay
     * @Description: Xử lý kết quả tại đây.
     * @Note: Sau khi test thành công thì chuyển thành method POST
     **/
    @GetMapping("/payment-result")
    public String paymentResult(Model model,
                                HttpServletRequest request,
                                @ModelAttribute PaymentResultDto paymentResultDto) {
        // Merchant/website TMĐT thực hiện kiểm tra sự toàn vẹn của dữ liệu (checksum) trước khi thực hiện các thao tác khác
        String result = "";
        try {
            String vnpOrderInfo = request.getParameter("vnp_OrderInfo");
            result = orderService.handleResult(request);
            model.addAttribute("message", result);
            model.addAttribute("code", vnpOrderInfo);

        } catch (Exception e) {
            out.print("{\"RspCode\":\"99\",\"Message\":\"Unknow error\"}");
            model.addAttribute("message", result);
        }
        return "/user/invoice/payment-result";
    }

    /**
     * @Summary: Tiến trình xử lý Payment getway
     * @Description: Trong page xác nâḥn thanh toán, người dùng bấm nút thanh toán.
     * @Exception:
     **/
    @PostMapping("/process-payment")
    public String processPayment(@RequestParam("orderId") String orderId) {
        // Xử lý thanh toán với dịch vụ thanh toán
        // Cập nhật trạng thái đơn hàng
        return "redirect:/order/success";
    }
}