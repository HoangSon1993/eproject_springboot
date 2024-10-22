package com.sontung.eproject_springboot.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;

import com.sontung.eproject_springboot.dto.ComboDTO;
import com.sontung.eproject_springboot.dto.ComboDetailDTO;
import com.sontung.eproject_springboot.entity.*;

public interface ComboService {
    // EntityManager entityManager;
    List<Product> getProducts();

    List<Combo> getCombos();

    List<Combo> getExpiringCombos();

    Combo createCombo(Combo combo);

    Combo createCombo1(ComboDTO comboDTO, String uniqueFilename);

    Combo getCombo(String comboId);

    int updateCombo(Combo combo, BigDecimal totalAmount, BigDecimal finalAmount);

    void removeCombo(String comboId);

    long countComBos();

    ComboDetail createComboDetail(ComboDetailDTO comboDetailDTO);

    // =====================================================================//
    // =========================User Combo Service==========================//
    // =====================================================================//
    Page<Combo> listCombos(int page, int size);

    List<Combo> getFeaturedCombo();

    List<Category> listCategories();

    List<Combo> listComboCategory(String categoryId);

    List<ComboDetail> getComboDetails(String comboId);

    Combo getComboById(String comboId);

    // Count combo for admin
    long countAdminComBos();

    Page<OrderDetail> getOrdersByDate(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate filterDate, int page, int size);

    long countOrderDetailInComboMgr(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate filterDate);

    List<OrderDetail> getOrdersByDate(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate filterDate);

    // Count combo for user
    long countUserComBos();

    Page<Combo> getCombos(int page, int size);

    Page<Combo> getCombosTypical(int pageNumber, int pageSize);
}
