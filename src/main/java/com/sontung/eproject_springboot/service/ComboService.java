package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.entity.Category;
import com.sontung.eproject_springboot.entity.Combo;
import com.sontung.eproject_springboot.entity.ComboDetail;
import com.sontung.eproject_springboot.entity.Product;
import com.sontung.eproject_springboot.repository.ICategoryRepository;
import com.sontung.eproject_springboot.repository.IComboDetailRepository;
import com.sontung.eproject_springboot.repository.IComboRepository;
import com.sontung.eproject_springboot.repository.IProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComboService {
    private final IComboRepository iComboRepository;
    private final IComboDetailRepository iComboDetailRepository;
    private final ICategoryRepository iCategoryRepository;
    //dùng đế test danh sách product
    private final IProductRepository iProductRepository;

    public ComboService(IComboRepository iComboRepository, IComboDetailRepository iComboDetailRepository, ICategoryRepository iCategoryRepository, IProductRepository iProductRepository) {
        this.iComboRepository = iComboRepository;
        this.iComboDetailRepository = iComboDetailRepository;
        this.iCategoryRepository = iCategoryRepository;
        this.iProductRepository = iProductRepository;
    }

    //EntityManager entityManager;
    public List<Product> getProducts() {
        return iProductRepository.findAll();
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

    public List<Category> listCategories(){
        return iCategoryRepository.findAll().stream().filter(c->c.getStatus()==1).collect(Collectors.toList());
    }

    public List<Combo> listComboCategory(String categoryId) {
        return iComboRepository.findCombosByStatusAndCategory(categoryId);
    }

    public List<ComboDetail> getComboDetails(String comboId) {
        return iComboDetailRepository.findByCombo_ComboId(comboId);
    }

    public Combo getComboById(String comboId) {
        return iComboRepository.findById(comboId)
                .orElse(null);
    }

//    public long countComboCategory(String categoryId){
//        return iComboRepository.countComboByCategory(categoryId);
//    }
}
