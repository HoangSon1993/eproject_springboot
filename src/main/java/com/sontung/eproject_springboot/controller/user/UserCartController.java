package com.sontung.eproject_springboot.controller.user;

import com.sontung.eproject_springboot.dto.request.CartUpdateRequest;
import com.sontung.eproject_springboot.exception.ProductNotFoundException;
import com.sontung.eproject_springboot.exception.UserNotFoundException;
import com.sontung.eproject_springboot.service.CartService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class UserCartController {

    private final CartService cartService;

    @Value("${aws.s3.bucket.url}")
    String s3BucketUrl;

    public UserCartController(CartService cartService) {
        this.cartService = cartService;
    }

    @ModelAttribute("s3BucketUrl")
    public String s3BucketUrl() {
        return s3BucketUrl;
    }

    @GetMapping("/index")
    public String getCarts(Model model) {
        model.addAttribute("cartDetail", cartService.getCarts());
        model.addAttribute("amount", cartService.getTotalAmount());
        return "/user/cart/index";
    }

    @PostMapping("/create")
    public String addComboToCart(@RequestParam String comboId, @RequestParam int quantity) {
        cartService.addComboToCart(comboId, quantity);
        return "redirect:/cart/index";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addProductToCart(@RequestParam("productId") String productId,
                                                                @RequestParam("quantity") int quantity) {
        Map<String, Object> response = new HashMap<>();
        try {
            cartService.addProductToCart(productId, quantity);
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

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateCart(@RequestBody CartUpdateRequest request) {
        // todo chưa hoàn thành, cần nhận được cartId
        Map<String, Object> response = new HashMap<>();
        try {
            cartService.updateProductQuantity(request.getId(), request.getQuantity());
            response.put("success", true);
            response.put("newTotal", cartService.getTotalAmount());

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Đã có lỗi không mong muốn xảy ra");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam("id") String id,
                                 @RequestParam("type") String type,
                                 RedirectAttributes redirectAttributes
    ) {
        try {
            if ("product".equals(type)) {
                cartService.removeProductFromCart(id);
                redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được xoá khỏi giỏ hàng");
            } else if ("combo".equals(type)) {
                cartService.removeComboFromCart(id);
                redirectAttributes.addFlashAttribute("message", "Combo đã được xoá khỏi giỏ hàng");
            } else {
                redirectAttributes.addFlashAttribute("error", "Loại không hợp lệ");
            }
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/cart/index";
    }
}
