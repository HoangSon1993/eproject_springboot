package com.sontung.eproject_springboot.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.sontung.eproject_springboot.entity.Category;
import com.sontung.eproject_springboot.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("")
    public String getCategories(Model model) {
        model.addAttribute("categories", categoryService.getCategories());
        return "/admin/category/index";
    }

    @GetMapping("/detail")
    public String getCategory(@RequestParam String id, Model model) {
        model.addAttribute("category", categoryService.getCategory(id));
        return "/admin/category/detail";
    }

    @GetMapping("/create")
    public String createCategory(Model model) {
        model.addAttribute("new_category", new Category());
        return "/admin/category/create";
    }

    @PostMapping("/create")
    public String createCategory(@ModelAttribute Category category) {
        categoryService.createCategory(category);
        return "redirect:/admin/category";
    }

    @GetMapping("/edit")
    public String editCategory(@RequestParam String id, Model model) {
        model.addAttribute("update_category", categoryService.getCategory(id));
        return "/admin/category/edit";
    }

    @PostMapping("/edit")
    public String editCategory(@RequestParam String id, @ModelAttribute Category category) {
        categoryService.editCategory(id, category);
        return "redirect:/admin/category";
    }

    @PostMapping("/delete")
    public String deleteCategory(@RequestParam String id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/category";
    }
}
