package com.sontung.eproject_springboot.controller.user;

import com.sontung.eproject_springboot.entity.Category;
import com.sontung.eproject_springboot.entity.Product;
import com.sontung.eproject_springboot.service.CategoryService;
import com.sontung.eproject_springboot.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("product")
public class UserProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    @Value("${aws.s3.bucket.url}")
    String s3BucketUrl;

    @ModelAttribute("s3BucketUrl")
    public String s3BucketUrl() {
        return s3BucketUrl;
    }

    @Autowired
    public UserProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/index")
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String categoryId,
            @RequestParam(defaultValue = "asc") String sort,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "productName"));
        int status = 1;
        Page<Product> products;
        if (categoryId == null || categoryId.isEmpty()) {
            products = productService.findByStatusAndProductNameContaining(status, search, pageable);
        } else {
            products = productService.findByStatusAndCategory_CategoryIdAndProductNameContaining(status, categoryId, search, pageable);
        }

        List<Category> categories = categoryService.getCategories();

        model.addAttribute("pageNumber", page); // Trang hiện tại, bắt đầu từ 0
        model.addAttribute("itemsPerpage", size); // Số mục trên mỗi trang
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("search", search); // Từ khoá tìm kiếm hiện tại
        model.addAttribute("categoryId", categoryId); // ID danh mục hiện tại
        model.addAttribute("sort", sort); // Hướng sắp xếp hiện tại

        return "/user/product/index";
    }
}
