package com.sontung.eproject_springboot.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sontung.eproject_springboot.dto.ComboDetailDTO;
import com.sontung.eproject_springboot.entity.Combo;
import com.sontung.eproject_springboot.entity.ComboDetail;
import com.sontung.eproject_springboot.entity.ComboDetailId;
import com.sontung.eproject_springboot.entity.Product;
import com.sontung.eproject_springboot.mapper.IComboDetailMapper;
import com.sontung.eproject_springboot.repository.IComboDetailRepository;
import com.sontung.eproject_springboot.repository.IComboRepository;
import com.sontung.eproject_springboot.repository.IProductRepository;
import com.sontung.eproject_springboot.service.ComboDetailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComboDetailServiceImpl implements ComboDetailService {
    private final IComboDetailRepository iComboDetailRepository;
    private final IComboDetailMapper icomboDetailMapper;
    private final IComboRepository iComboRepository;
    private final IProductRepository iProductRepository;

    @Override
    public ComboDetail createComboDetail(ComboDetailDTO comboDetailDTO) {
        ComboDetail comboDetail = icomboDetailMapper.toComboDetail(comboDetailDTO);
        Product product = iProductRepository
                .findById(comboDetailDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Error"));
        Combo combo =
                iComboRepository.findById(comboDetailDTO.getComboId()).orElseThrow(() -> new RuntimeException("Error"));
        ComboDetailId comboDetailId = new ComboDetailId(comboDetailDTO.getComboId(), comboDetailDTO.getProductId());
        comboDetail.setComboDetailId(comboDetailId);
        comboDetail.setCombo(combo);
        comboDetail.setProduct(product);
        iComboDetailRepository.save(comboDetail);
        return comboDetail;
    }

    @Override
    public List<ComboDetail> getComboDetails(String comboId) {
        return iComboDetailRepository.findByCombo_ComboId(comboId);
    }
}
