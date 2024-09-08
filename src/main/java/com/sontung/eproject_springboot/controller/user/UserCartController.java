package com.sontung.eproject_springboot.controller.user;

import com.sontung.eproject_springboot.dto.CartDetailDTO;
import com.sontung.eproject_springboot.dto.request.CartUpdateRequest;
import com.sontung.eproject_springboot.entity.Cart;
import com.sontung.eproject_springboot.exception.PriceChangedException;
import com.sontung.eproject_springboot.exception.ProductNotFoundException;
import com.sontung.eproject_springboot.exception.UserNotFoundException;
import com.sontung.eproject_springboot.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/cart")
@SessionAttributes("checkedItems")
@RequiredArgsConstructor
public class UserCartController {

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

    /**
     * @Summary:
     * @Description:
     * @Param:
     * @Return:
     * @Exception:
     **/
    @ModelAttribute("checkedItems")
    public List<String> initializeCheckedItems() {
        return new ArrayList<>();
    }

    /**
     * @Summary: Show all item trong Cart của User.
     * @Description: Xử lý lấy tất cả item mà người dùng đã thêm vào Cart.
     * @Param: List <String> checkedItems
     * @Return:
     * @Exception:
     **/
    @GetMapping("/index")
    public String getCarts(Model model,
                           @ModelAttribute("checkedItems") List<String> checkedItems,
                           HttpSession session) {
        List<CartDetailDTO> cartDetailDTOS = (List<CartDetailDTO>) session.getAttribute("cartItems");
        if (cartDetailDTOS != null) {
            session.removeAttribute("cartItems");
        }

        String warning = (String) session.getAttribute("warning");
        if (warning != null) {
            model.addAttribute("warning", warning);
            session.removeAttribute("warning");
        }
        if (cartDetailDTOS == null) {
            cartDetailDTOS = cartService.getCarts(userId, null);
        }

        // Cập nhật trạng thái checked cho các sản phẩm đã chọn
        cartDetailDTOS.forEach(item -> {
            if (checkedItems.contains(item.getId())) {
                item.setChecked(true);
            }
        });

        model.addAttribute("cartDetail", cartDetailDTOS);

        return "/user/cart/index";
    }

    /**
     * @Summary: Thêm combo vào Cart.
     * @Description: Cần xử lý thêm combo từ page Home, Combo, ComboDetail với số lượng > 1
     * @Param:
     * @Return:
     * @Exception:
     **/
    @PostMapping("/create")
    public String addComboToCart(
            RedirectAttributes redirectAttributes,
            @RequestParam String comboId,
            @RequestParam int quantity,
            @ModelAttribute("checkedItems") List<String> checkedItems) {
        try {
            Cart cart = cartService.addComboToCart(userId, comboId, quantity);
            // Thêm combo và danh sách checked
            if (!checkedItems.contains(cart.getCartId())) {
                checkedItems.add(cart.getCartId());
            }
            redirectAttributes.addFlashAttribute("message", "Thêm combo thành công.");
            return "redirect:/cart/index";
        } catch (RuntimeException ex) {
            log.error(ex.getMessage(), ex);
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/combo/index";
        }
    }

    /**
     * @Summary: Thêm sản phẩm vào giỏ hàng
     * @Description: Áp dụng cho cả 2 trường hợp.
     * Th1: Thêm 1 sản phẩm từ page Product, hoặc Home.
     * Th2: Thêm nhiều sản phẩm từ page Product detail.
     **/
    @PostMapping("/add-product")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addProductToCart(
            @RequestParam("productId") String productId,
            @RequestParam("quantity") int quantity,
            @ModelAttribute("checkedItems") List<String> checkedItems) {
        Map<String, Object> response = new HashMap<>();
        try {
            Cart cart = cartService.addProductToCart(userId, productId, quantity);

            // Thêm sản phẩm vào danh sách checked
            // Nếu sản Id sản phẩm chưa nằm trong checkedItems thì thêm vào
            if (!checkedItems.contains(cart.getCartId())) {
                checkedItems.add(cart.getCartId());
            }

            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException | UserNotFoundException ex) {
            response.put("success", false);
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Đã xảy ra lỗi không mong muốn");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/add-combo")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addComboToCart(
            @RequestParam("comboId") String comboId,
            @RequestParam("quantity") int quantity,
            @ModelAttribute("checkedItems") List<String> checkedItems) {
        Map<String, Object> response = new HashMap<>();
        try {
            Cart cart = cartService.addComboToCart(userId, comboId, quantity);

            // Thêm combo vào danh sách checked
            // Nếu sản Id combo chưa nằm trong checkedItems thì thêm vào
            if (!checkedItems.contains(cart.getCartId())) {
                checkedItems.add(cart.getCartId());
            }

            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException | UserNotFoundException ex) {
            response.put("success", false);
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Đã xảy ra lỗi không mong muốn");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * @Summary: Cập nhật lại số lượng của item trong giỏ hàng.
     * @Description: Xử lý cả 2 trường hợp người dùng nhấn nút tăng giảm số lượng,
     * hoặc người dùng nhập số lượng trực tiếp vào ô input.
     * @Param:
     **/
    @PostMapping("/updateQuantity")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateQuantity(
            @RequestBody CartUpdateRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = cartService.updateQuantity(request.getId(), request.getQuantity());
            response.put("success", success);

        } catch (RuntimeException ex) {
            log.error(ex.getMessage(), ex);
            response.put("success", false);
            response.put("message", ex.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * @Summary: Xử lý check/uncheck khi người dùng thao tác trên gỉo hàng.
     * @Description: Xử lý cả trường hợp người dùng check/uncheck trên 1 item hoặc là all item.
     * @Param:
     **/
    @PostMapping("/updateChecked")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCheckedItem(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "checked", required = false) Boolean checked,
            @RequestParam(value = "selectAll", required = false) Boolean selectAll,
            @RequestParam(value = "cartItems", required = false) List<String> cartItems,
            @ModelAttribute("checkedItems") List<String> checkedItems) {
        Map<String, Object> response = new HashMap<>();

        // Truong hop selectAll != null
        if (selectAll != null) {
            if (selectAll) {
                // case checkAll
                checkedItems.clear();
                checkedItems.addAll(cartItems);
                // ....

            } else {
                // case unCheckAll
                checkedItems.clear();
                // ....
            }
        } else {
            //Case old: check or unCheck one item

            // Cap nhat ds checkedItems
            if (checked) {
                if (!checkedItems.contains(id)) {
                    checkedItems.add(id);
                }
            } else {
                checkedItems.remove(id);
            }
        }
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    /**
     * @Summary: Xoá item ra khỏi Cart.
     * @Description: Sử lý xoá item ra khỏi Cart khi người dùng bấm vào nút remove,
     * hoặc trường hợp người dùng giảm số lượng về 0.
     **/
    @PostMapping("/remove")
    public String removeFromCart(
            @RequestParam("id") String id,
            RedirectAttributes redirectAttributes,
            @ModelAttribute("checkedItems") List<String> checkedItems) {
        try {
            // Xoá sản phẩm khỏi giỏ hàng
            cartService.removeItemFromCart(id);

            // Xoá sản phẩm khỏi danh sách checkedItems
            checkedItems.remove(id);
            redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được xoá khỏi giỏ hàng");
        } catch (RuntimeException ex) {
            log.error(ex.getMessage(), ex);
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/cart/index";
    }

    /**
     * @Summary: Show form checkout.
     * @Description: Kiểm tra sản phẩm mà người dùng check, show thông tin đơn hàng để người dùng kiểm tra quantity, price, total_amount.
     * @Param
     **/
    @GetMapping("/checkout")
    public String checkout_form(Model model,
                                HttpSession session,
                                @RequestParam(required = false) List<String> cartItems) {

        // Nếu không có sản phẩm nào được chọn, điều hướng về trang giỏ hàng
        if (cartItems == null || cartItems.isEmpty()) {
            model.addAttribute("message", "Vui lòng chọn sản phẩm để thanh toán.");
            return "redirect:/cart/index";
        }
        // Lấy thông tin sản phẩm từ các ID được chọn
        List<CartDetailDTO> cartDetailDTOS = cartService.getCartByIds(userId, cartItems);

        BigDecimal totalAmount = (BigDecimal) session.getAttribute("totalAmount");
        if (totalAmount != null) {
            session.removeAttribute("totalAmount");
        }
        // Truyền thông tin sang view để hiển thị trên trang checkout.
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("cartDetails", cartDetailDTOS);
        model.addAttribute("cartItems", cartItems);

        return "/user/cart/checkout";
    }

    /**
     * @Summary: Hàm trung gian sử lý khi người dùng checkout.
     * @Description: Tiếp nhận yêu cầu từ client bằng method 'POST'
     * Từ đây xử lý chuyển hướng lên method Get '/checkout'.
     * @Param: List <String> cartItems
     **/
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(@RequestBody List<String> cartItems, HttpSession session) {
        // Chuyển hướng sang GET để hiển thị trang checkout với các sản phẩm được chọn
        Map<String, Object> response = new HashMap<>();
        if (cartItems == null || cartItems.isEmpty()) {
            response.put("success", false);
            response.put("message", "Không có sản phẩm nào được chọn.");
        } else {
            // Lấy thông tin sản phẩm từ các ID được chọn
            List<CartDetailDTO> cartDetailDTOS = cartService.getCartByIds(userId, cartItems);

            try {
                // Trường hợp giá không đổi.

                BigDecimal totalAmount = cartService.getTotalAmount(cartDetailDTOS);
                session.setAttribute("totalAmount", totalAmount); // gửi totalAmount qua session

                String url = "/cart/checkout?cartItems=" + String.join(",", cartItems);
                response.put("success", true);
                response.put("url", url);
                return ResponseEntity.ok(response);
            } catch (PriceChangedException e) {
                // Trường hợp giá đã thay đổi.
                response.put("success", true);
                String url = "/cart/index";  // Điều hướng về trang giỏ hàng
                response.put("url", url);
                session.setAttribute("cartItems", cartDetailDTOS);
                session.setAttribute("warning", e.getMessage());
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.ok(response);
    }
}
