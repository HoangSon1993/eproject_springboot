package com.sontung.eproject_springboot.controller.user;

import com.sontung.eproject_springboot.dto.request.OrderDtoRequest;
import com.sontung.eproject_springboot.dto.request.PaymentResultDto;
import com.sontung.eproject_springboot.entity.Order;
import com.sontung.eproject_springboot.service.CartService;
import com.sontung.eproject_springboot.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.out;

@RequestMapping("/order")
@Controller("userOrderController")
@SessionAttributes("order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final CartService cartService;

    @Value("${aws.s3.bucket.url}")
    String s3BucketUrl;

    @Value("${user.id}")
    private String userId; // userId tạm

    /**
     * @Summary:
     * @Description:
     * @Param:
     * @Return:
     * @Exception:
     **/
    @ModelAttribute("s3BucketUrl")
    public String s3BucketUrl() {
        return s3BucketUrl;
    }

    @GetMapping("/detail/{code}")
    public String detail(@PathVariable("code") String code, Model model) {
        Order order = orderService.findByCodeAndAccountId(userId, code);
        if (order == null) {
            model.addAttribute("error", "Không tìm thấy đơn hàng.");
        }
        model.addAttribute("order", order);
        return "/user/order/detail";
    }

    /**
     * @Summary: Create Order and Order_detail
     * @Description: Sử lý thanh toán ở page cart/checkout sau khi người dùng kiểm tra đơn hàng,
     * và người dùng bấm nút thanh toán.
     **/
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody OrderDtoRequest orderDtoRequest,
                                                      BindingResult bindingResult,
                                                      HttpServletRequest req, HttpServletResponse resp,
                                                      HttpSession session) throws UnsupportedEncodingException {
        Map<String, Object> response = new HashMap<>();

        // Valid dữ liệu đầu vào.
        if (bindingResult.hasErrors()) {
            Map<String, String > errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            response.put("success", false);
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response); // Trả về lỗi
        }
        // 1. Create Order and OrderDetail in Transaction.
        Order order = orderService.createOrder(orderDtoRequest, userId);

        // 2. Delete cartItems from cart and Session.
        List<String> checkedItems = (List<String>) session.getAttribute("checkedItems");
        session.removeAttribute("checkedItems");

        cartService.removeCartItems(checkedItems);
        // 2. Call Vnpay Service to payment.
        try {
            String url = orderService.getVnpay(order, req, resp);
            // 3. Send Client notification
            response.put("success", true);
            response.put("url", url);
        } catch (UnsupportedEncodingException e) {
            // 3. Send Client notification
            response.put("success", false);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * @Summary: Nhận kết quả từ VnPay
     * @Description: Xử lý kết quả tại đây.
     * Note: Sau khi test thành công thì chuyển thành method POST
     **/
    @GetMapping("/payment-result")
    public String paymentResult(Model model,
                                HttpServletRequest request,
                                @ModelAttribute PaymentResultDto paymentResultDto) {
        // Merchant/website TMĐT thực hiện kiểm tra sự toàn vẹn của dữ liệu (checksum) trước khi thực hiện các thao tác khác
        String result = "";
        try {
            String orderInfo = request.getParameter("vnp_OrderInfo");
            String code = orderInfo.substring(orderInfo.length() - 8);
            result = orderService.handleResult(request, userId);
            model.addAttribute("message", result);
            model.addAttribute("code", code);

        } catch (Exception e) {
            out.print("{\"RspCode\":\"99\",\"Message\":\"Unknow error\"}");
            model.addAttribute("message", result);
        }
        return "/user/invoice/payment-result";
    }

    /**
     * @Summary: Tiến trình xử lý Payment getway
     * @Description: Trong page xác nhận thanh toán, người dùng bấm nút thanh toán.
     * @Exception:
     **/
    @PostMapping("/process-payment")
    public String processPayment(@RequestParam("orderId") String orderId) {
        // Xử lý thanh toán với dịch vụ thanh toán
        // Cập nhật trạng thái đơn hàng
        return "redirect:/order/success";
    }
}