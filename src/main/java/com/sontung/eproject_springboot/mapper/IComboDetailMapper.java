package com.sontung.eproject_springboot.mapper;

import org.mapstruct.Mapper;

import com.sontung.eproject_springboot.dto.ComboDetailDTO;
import com.sontung.eproject_springboot.entity.ComboDetail;

@Mapper(componentModel = "spring")
public interface IComboDetailMapper {
    ComboDetail toComboDetail(ComboDetailDTO comboDetailDTO);
}
