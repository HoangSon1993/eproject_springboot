package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.dto.ComboDTO;
import com.sontung.eproject_springboot.dto.ComboDetailDTO;
import com.sontung.eproject_springboot.entity.*;
import com.sontung.eproject_springboot.repository.IComboRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComboService {
    @Autowired
    @Lazy
    OrderService orderService;
    @Autowired
    OrderDetailService orderDetailService;
    private final IComboRepository iComboRepository;
    private final CategoryService categoryService;
    private final ComboDetailService comboDetailService;
    private final ProductService productService;

    public ComboService(IComboRepository iComboRepository,
                        ProductService productService,
                        CategoryService categoryService,
                        ComboDetailService comboDetailService) {
        this.iComboRepository = iComboRepository;
        this.productService = productService;
        this.categoryService = categoryService;
        this.comboDetailService = comboDetailService;
    }

    //EntityManager entityManager;
    public List<Product> getProducts() {
        return productService.findAll();
    }

    public List<Combo> getCombos() {
        return iComboRepository.findAll().stream().filter(c -> c.getStatus() == 2).collect(Collectors.toList());
    }

    public List<Combo> getExpiringCombos() {
        return iComboRepository.findAll().stream().filter(c -> c.getStatus() == 1).collect(Collectors.toList());
    }

    public Combo createCombo(Combo combo) {
        combo.setCreatedDate(LocalDate.now());
        combo.setUpdatedDate(LocalDate.now());
        combo.setStatus(2);
        return iComboRepository.save(combo);
    }

    public Combo createCombo1(ComboDTO comboDTO, String uniqueFilename) {
        Combo combo = new Combo();
        combo.setComboName(comboDTO.getComboName());
        combo.setDescription(comboDTO.getDescription());
        combo.setStartDate(comboDTO.getStartDate());
        combo.setEndDate(comboDTO.getEndDate());
        combo.setCreatedDate(LocalDate.now());
        combo.setUpdatedDate(LocalDate.now());
        combo.setStatus(2);
        combo.setImage(uniqueFilename);
        return iComboRepository.save(combo);
    }

    public Combo getCombo(String comboId) {
        return iComboRepository.findById(comboId).orElseThrow(() -> new RuntimeException("Not Found"));
    }

    public int updateCombo(Combo combo, BigDecimal totalAmount, BigDecimal finalAmount) {
        combo.setFinalAmount(finalAmount);
        combo.setTotalAmount(totalAmount);
        iComboRepository.save(combo);
        return 1;
    }

    public void removeCombo(String comboId) {
        //StoredProcedure query =  entityManager.createStoredProcedureQuery()
        Combo combo = iComboRepository.findById(comboId).orElseThrow(() -> new RuntimeException("Not Found"));
        combo.setStatus(1);
        iComboRepository.save(combo);
    }

    public long countComBos() {
        return iComboRepository.findAll().stream().filter(c -> c.getStatus() == 2).toList().size();
    }

    public ComboDetail createComboDetail(ComboDetailDTO comboDetailDTO) {
        return comboDetailService.createComboDetail(comboDetailDTO);
    }

    //=====================================================================//
    //=========================User Combo Service==========================//
    //=====================================================================//
    public Page<Combo> listCombos(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return iComboRepository.findByStatus(2, pageable);
    }

    // TODO: 07/08/2024  Lấy ra combo  nổi bật, nhưng tạm thời lấy tạm
    public List<Combo> getFeaturedCombo() {
        return iComboRepository.findAll().stream().limit(3).collect(Collectors.toList());
    }

    public List<Category> listCategories() {
        return categoryService.getCategories().stream().filter(c -> c.getStatus() == 1).collect(Collectors.toList());
    }

    public List<Combo> listComboCategory(String categoryId) {
        return iComboRepository.findCombosByStatusAndCategory(categoryId);
    }

    public List<ComboDetail> getComboDetails(String comboId) {
        return comboDetailService.getComboDetails(comboId);
    }

    public Combo getComboById(String comboId) {
        return iComboRepository.findById(comboId)
                .orElse(null);
    }

    //====Tìm kiếm các sản phầm được bán trong ngày được chọn
//    public List<Order> getOrdersByDate(@DateTimeFormat(pattern = "yyyy-MM-dd") Date filterDate){
//
//        return orderService.getOrdersByFilterDate(filterDate);
//    }
    public List<OrderDetail> getOrdersByDate(@DateTimeFormat(pattern = "yyyy-MM-dd") Date filterDate) {
        List<Order> orders = orderService.getOrdersByFilterDate(filterDate);
        List<String> orderIdList = new ArrayList<>();
        for (Order order : orders) {
            orderIdList.add(order.getOrderId());
        }
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (String orderId : orderIdList) {
            orderDetailList = orderDetailService.getOrderDetails(orderId);
        }
        return orderDetailList;
    }

}
