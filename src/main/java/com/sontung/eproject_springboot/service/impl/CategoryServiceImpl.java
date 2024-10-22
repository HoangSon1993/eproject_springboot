package com.sontung.eproject_springboot.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sontung.eproject_springboot.entity.Category;
import com.sontung.eproject_springboot.repository.ICategoryRepository;
import com.sontung.eproject_springboot.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final ICategoryRepository iCategoryRepository;

    @Override
    public List<Category> getCategories() {
        return iCategoryRepository.findAll();
    }

    @Override
    public int createCategory(Category category) {
        category.setCreatedDate(LocalDate.now());
        category.setUpdatedDate(LocalDate.now());
        iCategoryRepository.save(category);
        return 1;
    }

    @Override
    public Category getCategory(String id) {
        return iCategoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Not Found"));
    }

    @Override
    public Category editCategory(String id, Category updateCategory) {
        Category category = iCategoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Not Found"));
        category.setCategoryName(updateCategory.getCategoryName());
        category.setStatus(updateCategory.getStatus());
        category.setCreatedDate(updateCategory.getCreatedDate());
        category.setUpdatedDate(LocalDate.now());
        return iCategoryRepository.save(category);
    }

    @Override
    public void deleteCategory(String id) {
        iCategoryRepository.deleteById(id);
    }
}
