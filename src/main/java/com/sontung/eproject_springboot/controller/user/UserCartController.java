package com.sontung.eproject_springboot.controller.user;

import com.sontung.eproject_springboot.dto.CartDetailDTO;
import com.sontung.eproject_springboot.dto.request.CartUpdateRequest;
import com.sontung.eproject_springboot.entity.Cart;
import com.sontung.eproject_springboot.exception.ProductNotFoundException;
import com.sontung.eproject_springboot.exception.UserNotFoundException;
import com.sontung.eproject_springboot.service.CartService;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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

        List<CartDetailDTO> cartDetail = cartService.getCarts();

        // Cập nhật trạng thái checked cho các sản phẩm đã chọn
        cartDetail.forEach(item -> {
            if (checkedItems.contains(item.getId())) {
                item.setChecked(true);
            }
        });

        model.addAttribute("cartDetail", cartDetail);
        return "/user/cart/index";
    }

    @PostMapping("/create")
    public String addComboToCart(
            RedirectAttributes redirectAttributes,
            @RequestParam String comboId,
            @RequestParam int quantity,
            @ModelAttribute("checkedItems") List<String> checkedItems) {
        try {
            Cart cart = cartService.addComboToCart(comboId, quantity);
            // Thêm combo và danh sách checked
            if (!checkedItems.contains(cart.getCartId())) {
                checkedItems.add(cart.getCartId());
            }
            redirectAttributes.addFlashAttribute("message", "Thêm combo thành công.");
            return "redirect:/cart/index";
        } catch (RuntimeException ex) {
            log.error(ex.getMessage(), ex);
            //todo: hiển thị thông báo lỗi ở view
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/combo/index";
        }
    }

    @PostMapping("/add-product")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addProductToCart(
            @RequestParam("productId") String productId,
            @RequestParam("quantity") int quantity,
            @ModelAttribute("checkedItems") List<String> checkedItems) {
        Map<String, Object> response = new HashMap<>();
        try {
            Cart cart = cartService.addProductToCart(productId, quantity);

            // Thêm sản phẩm vào danh sách checked
            // Nếu sản Id sản phẩm chưa nằm trong checkedItems thì them vào
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
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/updateChecked")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCheckedItem(
            @RequestParam("id") String id,
            @RequestParam("checked") Boolean checked,
            @RequestParam("selectAll") Boolean selectAll,
            @RequestParam("cartItems") List<String> cartItems,
            @ModelAttribute("checkedItems") List<String> checkedItems) {
        Map<String, Object> response = new HashMap<>();
        // Cap nhat ds checkedItems
        if (checked) {
            if (!checkedItems.contains(id)) {
                checkedItems.add(id);
            }
        } else {
            checkedItems.remove(id);
        }

        response.put("success", true);
        return ResponseEntity.ok(response);
    }

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
}
