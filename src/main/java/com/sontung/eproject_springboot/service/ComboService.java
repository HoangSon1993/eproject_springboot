package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.dto.ComboDTO;
import com.sontung.eproject_springboot.dto.ComboDetailDTO;
import com.sontung.eproject_springboot.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface ComboService {
    //EntityManager entityManager;
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

    //=====================================================================//
    //=========================User Combo Service==========================//
    //=====================================================================//
    Page<Combo> listCombos(int page, int size);

    // TODO: 07/08/2024  Lấy ra combo  nổi bật, nhưng tạm thời lấy tạm
    List<Combo> getFeaturedCombo();

    List<Category> listCategories();

    List<Combo> listComboCategory(String categoryId);

    List<ComboDetail> getComboDetails(String comboId);

    Combo getComboById(String comboId);

    List<OrderDetail> getOrdersByDate(@DateTimeFormat(pattern = "yyyy-MM-dd") Date filterDate);
}
