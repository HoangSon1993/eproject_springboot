package com.sontung.eproject_springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sontung.eproject_springboot.entity.ComboDetail;
import com.sontung.eproject_springboot.entity.ComboDetailId;

@Repository
public interface IComboDetailRepository extends JpaRepository<ComboDetail, ComboDetailId> {
    List<ComboDetail> findByCombo_ComboId(String comboId);
}
