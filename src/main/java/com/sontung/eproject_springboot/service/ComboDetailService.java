package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.dto.ComboDetailDTO;
import com.sontung.eproject_springboot.entity.Combo;
import com.sontung.eproject_springboot.entity.ComboDetail;
import com.sontung.eproject_springboot.entity.ComboDetailId;
import com.sontung.eproject_springboot.entity.Product;
import com.sontung.eproject_springboot.mapper.IComboDetailMapper;
import com.sontung.eproject_springboot.repository.IComboDetailRepository;
import com.sontung.eproject_springboot.repository.IComboRepository;
import com.sontung.eproject_springboot.repository.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComboDetailService {
    @Autowired
    IComboDetailRepository iComboDetailRepository;
    @Autowired
    IComboDetailMapper icomboDetailMapper;
    @Autowired
    IComboRepository iComboRepository;
    @Autowired
    IProductRepository iProductRepository;
    public int createComboDetail(ComboDetailDTO comboDetailDTO){
        ComboDetail comboDetail = icomboDetailMapper.toComboDetail(comboDetailDTO);
        Product product = iProductRepository.findById(comboDetailDTO.getProductId()).orElseThrow(() -> new RuntimeException("Error"));
        Combo combo = iComboRepository.findById(comboDetailDTO.getComboId()).orElseThrow(() -> new RuntimeException("Error"));
        ComboDetailId comboDetailId = new ComboDetailId(comboDetailDTO.getComboId(), comboDetailDTO.getProductId());
        comboDetail.setId(comboDetailId);
        comboDetail.setCombo(combo);
        comboDetail.setProduct(product);
        iComboDetailRepository.save(comboDetail);
        return 1;
    }
    public List<ComboDetail> getComboDetails(String comboId){
        return iComboDetailRepository.findByIdComboId(comboId);
    }
}
