package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.entity.Category;
import com.sontung.eproject_springboot.repository.ICategoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CategoryService {
    private final ICategoryRepository iCategoryRepository;

    public CategoryService(ICategoryRepository iCategoryRepository) {
        this.iCategoryRepository = iCategoryRepository;
    }

    public List<Category> getCategories() {
        return iCategoryRepository.findAll();
    }

    public int createCategory(Category category) {
        category.setCreatedDate(LocalDate.now());
        category.setUpdatedDate(LocalDate.now());
        iCategoryRepository.save(category);
        return 1;
    }

    public Category getCategory(String id) {
        return iCategoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Not Found"));
    }

    public Category editCategory(String id, Category updateCategory) {
        Category category = iCategoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Not Found"));
        category.setCategoryName(updateCategory.getCategoryName());
        category.setStatus(updateCategory.getStatus());
        category.setCreatedDate(updateCategory.getCreatedDate());
        category.setUpdatedDate(LocalDate.now());
        return iCategoryRepository.save(category);
    };

    public void deleteCategory(String id) {
        iCategoryRepository.deleteById(id);
    }
}
