package com.sontung.eproject_springboot.repository;

import com.sontung.eproject_springboot.dto.ComboInvoiceDTO;
import com.sontung.eproject_springboot.entity.Combo;
import com.sontung.eproject_springboot.entity.InvoiceDetail;
import com.sontung.eproject_springboot.entity.InvoiceDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IInvoiceDetailRepository extends JpaRepository<InvoiceDetail, InvoiceDetailId> {
    List<InvoiceDetail> findByIdInvoiceId(String invoiceId);

    @Query("SELECT DISTINCT c " +
            "FROM Combo c " +
            "JOIN InvoiceDetail id ON id.comboId = c.comboId " +
            "WHERE id.invoice.invoiceId = :invoiceId")
    List<Combo> findCombosByInvoiceId(@Param("invoiceId") String invoiceId);

    @Query("SELECT new com.sontung.eproject_springboot.dto.ComboInvoiceDTO(c, MAX(id.comboQuantity)) " +
            "FROM Combo c " +
            "JOIN InvoiceDetail id ON id.comboId = c.comboId " +
            "WHERE id.invoice.invoiceId = :invoiceId " +
            "GROUP BY c.comboId")
    List<ComboInvoiceDTO> findCombosWithUniqueQuantityByInvoiceId(@Param("invoiceId") String invoiceId);

}
