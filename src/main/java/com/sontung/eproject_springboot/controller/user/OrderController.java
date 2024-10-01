package com.sontung.eproject_springboot.controller.user;

import com.sontung.eproject_springboot.dto.request.OrderDtoRequest;
import com.sontung.eproject_springboot.dto.request.PaymentResultDto;
import com.sontung.eproject_springboot.entity.Account;
import com.sontung.eproject_springboot.entity.Order;
import com.sontung.eproject_springboot.entity.OrderDetail;
import com.sontung.eproject_springboot.enums.OrderStatus;
import com.sontung.eproject_springboot.repository.SearchRepository;
import com.sontung.eproject_springboot.service.CartService;
import com.sontung.eproject_springboot.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.*;

import static java.lang.System.out;

@RequestMapping("/order")
@Controller("userOrderController")
@SessionAttributes("order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final CartService cartService;
    private final SearchRepository searchRepository;

    /**
     * @Summary:
     * @Description:
     * @Param:
     * @Return:
     **/
    @GetMapping("/index")
    public String index(Model model,
                        @RequestParam(defaultValue = "0") int pageNo,
                        @Min(5) @RequestParam(defaultValue = "5") int pageSize,
                        @RequestParam(defaultValue = "") String search,
                        @RequestParam(defaultValue = "") String sortBy,
                        @RequestParam(defaultValue = "") String status,
                        @ModelAttribute("loggedInUser") Account account) {
        if (pageNo < 0) pageNo = 0;

        Page<Order> orders = searchRepository.getAllOrderWithSortByColoumAndSearchCriteriaBuider(pageNo, pageSize, search, status, sortBy, account.getAccountId());
        model.addAttribute("orders", orders);
        // Thêm đường dẫn URL cho phân trang
        model.addAttribute("pageUrl", "/order/index");
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("search", search);

        // Lấy danh sách các trạng thái đơn hàng
        Map<String, String> statuses = OrderStatus.toMap();

        /**
         * Có thể xử lý ở server thay vì thymeleaf
         statuses.remove(OrderStatus.ORDERED.name());
         statuses.put("All","");
         **/

        model.addAttribute("statuses", statuses);
        model.addAttribute("selectedStatus", status);
        return "user/order/index";
    }

    /**
     * @Summary:
     * @Description:
     * @Param:
     * @Return:
     **/
    @GetMapping("/detail")
    public String detail(@RequestParam("code") String code,
                         Model model,
                         @ModelAttribute("loggedInUser") Account account) {
        Order order = orderService.findByCodeAndAccountId(account.getAccountId(), code);
        if (order == null) {
            model.addAttribute("error", "Không tìm thấy đơn hàng.");
        }
        Set<String> orderItems = new HashSet<>();
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            orderItems.add(orderDetail.getOrderDetailId());
        }

        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderItems);
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
                                                      HttpSession session,
                                                      @ModelAttribute("loggedInUser") Account account) throws UnsupportedEncodingException {
        Map<String, Object> response = new HashMap<>();

        // Valid dữ liệu đầu vào.
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            response.put("success", false);
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response); // Trả về lỗi
        }
        // 1. Create Order and OrderDetail in Transaction.
        Order order = orderService.createOrder(orderDtoRequest, account.getAccountId());

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
     * @Summary: Xử lý thanh toán lại.
     * @Description: Tại Page Order_Index, hoặc Order_Detail mà đơn hàng status = PEDDING mà người dùng muốn thanh toán lại.
     **/
    @PostMapping("/re-create")
    public String reCreate(@RequestParam String code,
                           HttpServletRequest req,
                           HttpServletResponse resp,
                           @ModelAttribute("loggedInUser") Account account) {
        Order order = orderService.findByCodeAndAccountId(account.getAccountId(), code);
        if (order == null) {
            // Gui kem thong bao.
            return "redirect:/order/index";
        }
        // 2. Call VNPay Service to payment
        try {
            String url = orderService.getVnpay(order, req, resp);
            return "redirect:" + url;
        } catch (Exception e) {
            return "redirect:/order/index";
        }
    }

    /**
     * @Summary: Nhận kết quả từ VnPay
     * @Description: Xử lý kết quả tại đây.
     * Note: Sau khi test thành công thì chuyển thành method POST
     **/
    @GetMapping("/payment-result")
    public String paymentResult(Model model,
                                HttpServletRequest request,
                                @ModelAttribute PaymentResultDto paymentResultDto,
                                @ModelAttribute("loggedInUser") Account account) {
        // Merchant/website TMĐT thực hiện kiểm tra sự toàn vẹn của dữ liệu (checksum) trước khi thực hiện các thao tác khác
        String result = "";
        try {
            String orderInfo = request.getParameter("vnp_OrderInfo");
            String code = orderInfo.substring(orderInfo.length() - 8);
            result = orderService.handleResult(request, account.getAccountId());
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