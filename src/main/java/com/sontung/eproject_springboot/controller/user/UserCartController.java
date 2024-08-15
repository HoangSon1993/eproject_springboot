package com.sontung.eproject_springboot.controller.user;

import com.sontung.eproject_springboot.dto.request.CartUpdateRequest;
import com.sontung.eproject_springboot.entity.Cart;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
@SessionAttributes("checkedItems")
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

    @ModelAttribute("checkedItems")
    public List<String> initializeCheckedItems() {
        return new ArrayList<>();
    }

    @GetMapping("/index")
    public String getCarts(Model model, @ModelAttribute("checkedItems") List<String> checkedItems) {

        model.addAttribute("cartDetail", cartService.getCarts());

        // Cập nhật trạng thái checked cho các sản phẩm đã chọn
        cartService.getCarts().forEach(item -> {
            if (checkedItems.contains(item.getId())) {
                item.setChecked(true);
            }
        });

        model.addAttribute("amount", cartService.getTotalAmount(checkedItems));
        return "/user/cart/index";
    }

    @PostMapping("/create")
    public String addComboToCart(@RequestParam String comboId, @RequestParam int quantity) {
        cartService.addComboToCart(comboId, quantity);
        return "redirect:/cart/index";
    }

    @PostMapping("/add-product")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addProductToCart(@RequestParam("productId") String productId,
                                                                @RequestParam("quantity") int quantity,
                                                                @ModelAttribute("checkedItems") List<String> checkedItems) {
        Map<String, Object> response = new HashMap<>();
        try {
           Cart cart = cartService.addProductToCart(productId, quantity);

            // Thêm sản phẩm vào danh sách checked
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

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateCart(
            @RequestBody CartUpdateRequest request,
            @ModelAttribute("checkedItems") List<String> checkedItems) {
        // todo chưa hoàn thành, cần nhận được cartId
        Map<String, Object> response = new HashMap<>();
        try {
            // Cập nhật số lượng sản phẩm
            var amount = cartService.updateQuantity(request.getId(), request.getQuantity());

            // Kiểm tra và cập nhật danh sách checkedItems
            if (request.isChecked()) {
                if (!checkedItems.contains(request.getId())) {
                    checkedItems.add(request.getId());
                } else {
                    checkedItems.remove(request.getId());
                }
            }


            var totalAmount = cartService.getTotalAmount(checkedItems);
            response.put("success", true);
            response.put("amount", amount);
            response.put("newTotal", totalAmount);

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
                                 RedirectAttributes redirectAttributes,
                                 @ModelAttribute("checkedItems") List<String> checkedItems
    ) {
        try {
            // Xoá sản phẩm khỏi giỏ hàng
            cartService.removeItemFromCart(id);

            // Xoá sản phẩm khỏi danh sách checkedItems
            checkedItems.remove(id);
            redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được xoá khỏi giỏ hàng");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/cart/index";
    }
}
