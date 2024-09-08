package com.sontung.eproject_springboot.controller.user;

import com.sontung.eproject_springboot.entity.Category;
import com.sontung.eproject_springboot.entity.Product;
import com.sontung.eproject_springboot.service.CategoryService;
import com.sontung.eproject_springboot.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller(value = "userProductController")

@RequestMapping("product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    @Value("${aws.s3.bucket.url}")
    String s3BucketUrl;

    @ModelAttribute("s3BucketUrl")
    public String s3BucketUrl() {
        return s3BucketUrl;
    }

    @ModelAttribute("categories")
    public List<Category> populateCategories() {
        return categoryService.getCategories();
    }

    @ModelAttribute("categoryProductCounts")
    public Map<String, Integer> categoryProductCount() {
        List<Category> categories = populateCategories();
        Map<String, Integer> categoryProductCounts = new HashMap<>();

        for (Category category : categories) {
            int productCount = productService.countByCategory(category.getCategoryId());
            categoryProductCounts.put(category.getCategoryId(), productCount);
        }
        return categoryProductCounts;
    }

//    @ModelAttribute("search")
//    public String search(@RequestParam(required = false, defaultValue = "") String search) {
//        return search;
//    }

    @GetMapping("/index")
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String categoryId,
            @RequestParam(defaultValue = "asc") String sort,
            Model model
    ) {

        // Todo: Tối ưu hoá search, sort, filter bằng cách custom query.
        // productService.getAllProductWithSortByColumnAndSearch(page, size, search, sort);


        List<Sort.Order> sorts = new ArrayList<>();
        if(sort.equals("asc")){
            sorts.add(new Sort.Order(Sort.Direction.ASC, "productName"));
        }else{
            sorts.add(new Sort.Order(Sort.Direction.DESC, "productName"));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sorts));

        int status = 1;
        Page<Product> products;
        if (categoryId == null || categoryId.isEmpty()) {
            products = productService.findByStatusAndProductNameContaining(status, search, pageable);
        } else {
            products = productService.findByStatusAndCategory_CategoryIdAndProductNameContaining(status, categoryId, search, pageable);
        }

        model.addAttribute("pageNumber", page); // Trang hiện tại, bắt đầu từ 0
        model.addAttribute("itemsPerpage", size); // Số mục trên mỗi trang
        model.addAttribute("products", products);
        model.addAttribute("search", search); // Từ khoá tìm kiếm hiện tại
        model.addAttribute("categoryId", categoryId); // ID danh mục hiện tại
        model.addAttribute("sort", sort); // Hướng sắp xếp hiện tại

        return "/user/product/index";
   }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Product> productOptional = productService.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            model.addAttribute("product", product);
            return "user/product/detail";
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm");
            return "redirect:/product/index";
        }
    }
}
