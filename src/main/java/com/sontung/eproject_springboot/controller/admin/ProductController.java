package com.sontung.eproject_springboot.controller.admin;

import com.sontung.eproject_springboot.entity.Category;
import com.sontung.eproject_springboot.entity.Product;
import com.sontung.eproject_springboot.service.CategoryService;
import com.sontung.eproject_springboot.service.ProductService;
import com.sontung.eproject_springboot.service.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/product")
@SessionAttributes("categories")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final S3Service s3Service;

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

    @GetMapping
    public String index(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "5") int size,
                        Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.findAll(pageable);
        model.addAttribute("pageNumber", page); // Trang hiện tại, bắt đầu từ 0
        model.addAttribute("itemsPerPage", size); // Số mục trên mỗi trang
        model.addAttribute("products", products);
        return "admin/product/index";
    }

    @GetMapping("/create")
    public String createProductForm(Model model) {
        Product product = new Product();
        product.setStatus(1);
        model.addAttribute("product", product);
        return "/admin/product/create";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute Product product,
                                Errors errors,
                                @RequestParam("category.categoryId") String categoryId,
                                @RequestParam("file") MultipartFile image,
                                @ModelAttribute("categories") List<Category> categories,
                                RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            return "admin/product/create";
        }

        Optional<Category> categoryOptional = categories.stream()
                .filter(category -> category.getCategoryId().equals(categoryId)).findFirst();
        if (categoryOptional.isPresent()) {
            product.setCategory(categoryOptional.get());
            // Xử lý upload file lên S3
            if (!image.isEmpty()) {
                try {
                    String uniqueFilename = s3Service.generateUniqueFilename(image);

                    // Gọi Service upload file
                    s3Service.uploadFile(image, uniqueFilename);
                    product.setImage(uniqueFilename);
                    productService.save(product);
                    redirectAttributes.addFlashAttribute("message", "File Successfully Upload");
                    return "redirect:/admin/product";
                } catch (AwsServiceException e) {
                    redirectAttributes.addFlashAttribute("error", "AWS Service Exception: " + e.getMessage());
                } catch (SdkClientException e) {
                    redirectAttributes.addFlashAttribute("error", "SDK Client Exception: " + e.getMessage());
                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("error", "IOException: " + e.getMessage());
                }
                return "error";
            }
            // Case image is Null
            return "redirect:/admin/product";
        } else {
            // Case Category Not Found
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy Danh mục sản phẩm");
            return "error";
        }
    }

    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable("id") String product_id, Model model) {
        Optional<Product> product = productService.findById(product_id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "admin/product/edit";
        } else {
            return "error";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable String id,
                                @Valid @ModelAttribute Product productDetails,
                                Errors errors,
                                @RequestParam("file") MultipartFile image,
                                @ModelAttribute("categories") List<Category> categories,
                                @RequestParam("category.categoryId") String categoryId,
                                RedirectAttributes redirectAttributes,
                                Model model
    ) {
        if (errors.hasErrors()) {
            model.addAttribute("product", productDetails);
            return "admin/product/edit";
        }

        Optional<Product> optionalProduct = productService.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();

            Optional<Category> categoryOptional = categories.stream().filter(
                    category -> category.getCategoryId().equals(categoryId)
            ).findFirst();
            categoryOptional.ifPresent(product::setCategory);

            product.setProductName(productDetails.getProductName());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            product.setStatus(productDetails.getStatus());
            product.setCreatedDate(productDetails.getCreatedDate());
            product.setUpdatedDate(productDetails.getUpdatedDate());
            product.setCategory(productDetails.getCategory());
            //product.setCart(productDetails.getCart());
            product.setComboDetails(productDetails.getComboDetails());
//            product.setInvoiceDetails(productDetails.getInvoiceDetails());

            // Xử lý ảnh mới nếu có
            if (!image.isEmpty()) {
                try {
                    // Lấy hình ảnh hiện tại
                    String existsImageName = product.getImage();
                    if (existsImageName != null && !existsImageName.isEmpty()) {
                        s3Service.uploadFile(image, existsImageName);
                    } else {
                        // Nếu không có hình ảnh hiện tại, tải lên tệp mới với tên mới
                        s3Service.uploadFile(image, existsImageName);
                        product.setImage(image.getOriginalFilename());
                    }
                } catch (AwsServiceException e) {
                    redirectAttributes.addFlashAttribute("error", "AWS Service Exception: " + e.getMessage());
                    return "error";
                } catch (SdkClientException e) {
                    redirectAttributes.addFlashAttribute("error", "SDK Client Exception: " + e.getMessage());
                    return "error";
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("error", "Exception: " + e.getMessage());
                    return "error";
                }
            }
            productService.save(product);
            return "redirect:/admin/product";
        } else {
            return "error";
        }

    }

    @GetMapping("detail/{id}")
    public String editProdcut(@PathVariable("id") String productId, Model model) {
        Optional<Product> product = productService.findById(productId);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "admin/product/detail";
        }
        model.addAttribute("error", "Không tìm thấy sản phẩm");
        return "admin/product/index";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable String id,
                                RedirectAttributes redirectAttributes
    ) {
        // TODO: 29/7/24 action này để xoá hẳn 1 sản phẩm và hình ảnh trong AWS 
        Optional<Product> optionalProduct = productService.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();

            // ===== Xoá hình ảnh khỏi S3 =====
            if (product.getImage() != null) {
                try {
                    s3Service.deleteFile(product.getImage());
                } catch (AwsServiceException e) {
                    redirectAttributes.addFlashAttribute("error", "AWS Service Exception: " + e.getMessage());
                    return "error";
                } catch (SdkClientException e) {
                    redirectAttributes.addFlashAttribute("error", "SDK Client Exception: " + e.getMessage());
                    return "error";
                }
            }
            // ===== End xoá hình ảnh khỏi S3 =====

            productService.deleteById(id);
            return "redirect:/admin/product";
        } else {
            return "error";
        }
    }

    @PostMapping("/delete2/{id}")
    public String deleteProduct2(@PathVariable("id") String productId,
                                 RedirectAttributes redirectAttributes,
                                 Model model
    ) {
        // TODO: 29/7/24 Sau khi test thành công sẽ sử dụng action này để change status chứ k xoá hẳn product 
        Optional<Product> productOptional = productService.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            product.setStatus(0);

            productService.save(product);
            model.addAttribute("message", "Xoá sản phẩm thành công!");
        } else {
            model.addAttribute("error", "Sản phẩm không tìm thấy!");
        }
        return "redirect:/admin/product";
    }

}
