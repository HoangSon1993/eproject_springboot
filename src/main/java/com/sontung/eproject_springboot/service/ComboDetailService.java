package com.sontung.eproject_springboot.service;

import java.util.List;

import com.sontung.eproject_springboot.dto.ComboDetailDTO;
import com.sontung.eproject_springboot.entity.ComboDetail;

public interface ComboDetailService {
    ComboDetail createComboDetail(ComboDetailDTO comboDetailDTO);

    List<ComboDetail> getComboDetails(String comboId);
}
