package com.sontung.eproject_springboot.repository;

import com.sontung.eproject_springboot.entity.ComboDetail;
import com.sontung.eproject_springboot.entity.ComboDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface IComboDetailRepository extends JpaRepository<ComboDetail, ComboDetailId> {
    List<ComboDetail> findByCombo_ComboId(String comboId);
}
