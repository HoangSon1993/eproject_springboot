package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getCategories();

    int createCategory(Category category);

    Category getCategory(String id);

    Category editCategory(String id, Category updateCategory);

    void deleteCategory(String id);
}
