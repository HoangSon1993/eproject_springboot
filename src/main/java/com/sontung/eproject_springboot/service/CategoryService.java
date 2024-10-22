package com.sontung.eproject_springboot.service;

import java.util.List;

import com.sontung.eproject_springboot.entity.Category;

public interface CategoryService {
    List<Category> getCategories();

    int createCategory(Category category);

    Category getCategory(String id);

    Category editCategory(String id, Category updateCategory);

    void deleteCategory(String id);
}
