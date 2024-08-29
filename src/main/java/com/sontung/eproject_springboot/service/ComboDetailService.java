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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComboDetailService {
    private final IComboDetailRepository iComboDetailRepository;
    private final IComboDetailMapper icomboDetailMapper;
    private final IComboRepository iComboRepository;
    private final IProductRepository iProductRepository;

    public ComboDetailService(IComboDetailRepository iComboDetailRepository, IComboDetailMapper icomboDetailMapper, IComboRepository iComboRepository, IProductRepository iProductRepository) {
        this.iComboDetailRepository = iComboDetailRepository;
        this.icomboDetailMapper = icomboDetailMapper;
        this.iComboRepository = iComboRepository;
        this.iProductRepository = iProductRepository;
    }

    public ComboDetail createComboDetail(ComboDetailDTO comboDetailDTO) {
        ComboDetail comboDetail = icomboDetailMapper.toComboDetail(comboDetailDTO);
        Product product = iProductRepository.findById(comboDetailDTO.getProductId()).orElseThrow(() -> new RuntimeException("Error"));
        Combo combo = iComboRepository.findById(comboDetailDTO.getComboId()).orElseThrow(() -> new RuntimeException("Error"));
        ComboDetailId comboDetailId = new ComboDetailId(comboDetailDTO.getComboId(), comboDetailDTO.getProductId());
        comboDetail.setComboDetailId(comboDetailId);
        comboDetail.setCombo(combo);
        comboDetail.setProduct(product);
        iComboDetailRepository.save(comboDetail);
        return comboDetail;
    }

    public List<ComboDetail> getComboDetails(String comboId) {
        return iComboDetailRepository.findByCombo_ComboId(comboId);
    }
}
