package com.sontung.eproject_springboot.controller.user;

import jakarta.validation.constraints.Min;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sontung.eproject_springboot.entity.Combo;
import com.sontung.eproject_springboot.service.ComboService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/combo")
@RequiredArgsConstructor
public class UserComboController {
    private final ComboService comboService;

    @GetMapping("/index")
    public String getCombos(
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @Min(5) @RequestParam(defaultValue = "9") int size) {
        int p = 1;
        if (page > 1) p = page;
        Page<Combo> comboList = comboService.listCombos(p, size);
        model.addAttribute("combos", comboList);
        model.addAttribute("featuredCombos", comboService.getFeaturedCombo());
        model.addAttribute("categories", comboService.listCategories());
        long totalItems = comboService.countUserComBos();
        int totalPages = (int) (Math.ceil((double) totalItems / size));
        model.addAttribute("currentPage", p);
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
        model.addAttribute("combos", comboService.getCombos(1, 2));
        model.addAttribute("featuredCombos", comboService.getFeaturedCombo());
        model.addAttribute("combo", comboService.getCombo(comboId));
        model.addAttribute("categories", comboService.listCategories());
        model.addAttribute("comboDetails", comboService.getComboDetails(comboId));
        return "user/combo/detail";
    }
}
