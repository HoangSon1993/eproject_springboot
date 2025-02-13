package com.sontung.eproject_springboot.controller.user;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.constraints.Min;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sontung.eproject_springboot.entity.Category;
import com.sontung.eproject_springboot.entity.Product;
import com.sontung.eproject_springboot.repository.SearchRepository;
import com.sontung.eproject_springboot.service.CategoryService;
import com.sontung.eproject_springboot.service.ProductService;

import lombok.RequiredArgsConstructor;

@Controller(value = "userProductController")
@RequestMapping("product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final SearchRepository searchRepository;

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

    @GetMapping("/index")
    public String index(
            @RequestParam(defaultValue = "0") int pageNo,
            @Min(5) @RequestParam(defaultValue = "9") int pageSize,
            @RequestParam(defaultValue = "0") int amongPrice,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String categoryId,
            @RequestParam(defaultValue = "productName:asc") String sortBy,
            Model model) {
        if (pageNo < 0) pageNo = 0;

        List<Sort.Order> sorts = new ArrayList<>();
        if (StringUtils.hasLength(sortBy)) {
            // productName:asc|desc
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                } else {
                    sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }

        //        if (sortBy.equals("asc")) {
        //            sorts.add(new Sort.Order(Sort.Direction.ASC, "productName"));
        //        } else {
        //            sorts.add(new Sort.Order(Sort.Direction.DESC, "productName"));
        //        }

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sorts));

        int status = 1;
        Page<Product> products;

        products = searchRepository.findByStatusAndProductNameContainingAndPriceFilter(
                status, categoryId, search, amongPrice, pageable);

        model.addAttribute("pageNumber", pageNo); // Trang hiện tại, bắt đầu từ 0
        model.addAttribute("itemsPerpage", pageSize); // Số mục trên mỗi trang
        model.addAttribute("products", products);
        model.addAttribute("search", search); // Từ khoá tìm kiếm hiện tại
        model.addAttribute("categoryId", categoryId); // ID danh mục hiện tại
        model.addAttribute("sortBy", sortBy); // Hướng sắp xếp hiện tại
        model.addAttribute("amongPrice", amongPrice);

        return "/user/product/index";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Product> productOptional = productService.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            model.addAttribute("product", product);

            // Lấy danh sách sản phẩm tương tự dựa vào categoryId.
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "productName"));
            Page<Product> productsSimilar = searchRepository.findByStatusAndProductNameContainingAndPriceFilter(
                    1, product.getCategory().getCategoryId(), "", 500, pageable);
            model.addAttribute("productsSimilar", productsSimilar);
            return "user/product/detail";
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm");
            return "redirect:/product/index";
        }
    }
}
