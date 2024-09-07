package com.sontung.eproject_springboot.controller.user;

import com.sontung.eproject_springboot.entity.Combo;
import com.sontung.eproject_springboot.service.ComboService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/combo")
@RequiredArgsConstructor
public class UserComboController {
    private final ComboService comboService;
    @Value("${aws.s3.bucket.url}")
    String s3BucketUrl;

    @ModelAttribute("s3BucketUrl")
    public String s3BucketUrl() {
        return s3BucketUrl;
    }

    @GetMapping("/index")
    public String getCombos(Model model,
                            @RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "9") int size) {
        Page<Combo> comboList = comboService.listCombos(page, size);
        model.addAttribute("combos", comboList);
        model.addAttribute("featuredCombos", comboService.getFeaturedCombo());
        model.addAttribute("categories", comboService.listCategories());
        long totalItems = comboService.countComBos();
        int totalPages = (int) (Math.ceil((double) totalItems / size));
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", totalPages);
        return "user/combo/index";
    }

    @GetMapping("/combo-cate")
    public String getCombosCategory(@RequestParam String categoryId, Model model) {
        model.addAttribute("combos", comboService.listComboCategory(categoryId));
        model.addAttribute("featuredCombos", comboService.getFeaturedCombo());
        model.addAttribute("categories", comboService.listCategories());
        return "user/combo/combo_category";
    }

    @GetMapping("/detail")
    public String getCombo(@RequestParam String comboId, Model model) {
        // TODO: 06/08/2024 Chỗ này cần lấy ra các combo nổi bật, hiện tại đang lấy tạm tất cả các combo, khi có phần hóa đơn sẽ quay lại làm
        model.addAttribute("combos", comboService.getCombos());
        model.addAttribute("featuredCombos", comboService.getFeaturedCombo());
        model.addAttribute("combo", comboService.getCombo(comboId));
        model.addAttribute("categories", comboService.listCategories());
        model.addAttribute("comboDetails", comboService.getComboDetails(comboId));
        return "user/combo/detail";
    }
}
