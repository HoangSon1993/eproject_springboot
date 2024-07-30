package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.entity.Combo;
import com.sontung.eproject_springboot.entity.Product;
import com.sontung.eproject_springboot.repository.IComboRepository;
import com.sontung.eproject_springboot.repository.IProductRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComboService {
    @Autowired
    IComboRepository iComboRepository;
    //dùng đế test danh sách product
    @Autowired
    IProductRepository iProductRepository;
    //EntityManager entityManager;
    public List<Product> getProducts(){
      return iProductRepository.findAll();
    };
    public List<Combo> getCombos(){
        return iComboRepository.findAll().stream().filter(c->c.getStatus()==2).collect(Collectors.toList());
    };
    public Combo createCombo(Combo combo){
        combo.setCreatedDate(LocalDate.now());
        combo.setUpdatedDate(LocalDate.now());
        combo.setStatus(2);
        return iComboRepository.save(combo);
    }
    public Combo getCombo(String comboId){
        return iComboRepository.findById(comboId).orElseThrow(() -> new RuntimeException("Not Found"));
    }

    public int updateCombo(Combo combo, BigDecimal totalAmount, BigDecimal finalAmount){
        combo.setFinalAmount(finalAmount);
        combo.setTotalAmount(totalAmount);
        iComboRepository.save(combo);
        return 1;
    }

    public void removeCombo(String comboId){
        //StoredProcedure query =  entityManager.createStoredProcedureQuery()
        Combo combo = iComboRepository.findById(comboId).orElseThrow(() -> new RuntimeException("Not Found"));
        combo.setStatus(1);
        iComboRepository.save(combo);
    }
}
