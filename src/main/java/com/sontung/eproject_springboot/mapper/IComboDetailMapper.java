package com.sontung.eproject_springboot.mapper;

import com.sontung.eproject_springboot.dto.ComboDetailDTO;
import com.sontung.eproject_springboot.entity.ComboDetail;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IComboDetailMapper {
    ComboDetail toComboDetail(ComboDetailDTO comboDetailDTO);
}
