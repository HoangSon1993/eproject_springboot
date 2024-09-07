package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.dto.ComboDetailDTO;
import com.sontung.eproject_springboot.entity.ComboDetail;

import java.util.List;

public interface ComboDetailService {
    ComboDetail createComboDetail(ComboDetailDTO comboDetailDTO);

    List<ComboDetail> getComboDetails(String comboId);
}
