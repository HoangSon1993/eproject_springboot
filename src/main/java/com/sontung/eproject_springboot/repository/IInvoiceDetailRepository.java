package com.sontung.eproject_springboot.repository;

import com.sontung.eproject_springboot.entity.InvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IInvoiceDetailRepository extends JpaRepository<InvoiceDetail, String> {
//    List<InvoiceDetail> findByIdInvoiceId(String invoiceId);
//
//    @Query("SELECT DISTINCT c " +
//            "FROM Combo c " +
//            "JOIN InvoiceDetail id ON id.comboId = c.comboId " +
//            "WHERE id.invoice.invoiceId = :invoiceId")
//    List<Combo> findCombosByInvoiceId(@Param("invoiceId") String invoiceId);
//
//    @Query("SELECT new com.sontung.eproject_springboot.dto.ComboInvoiceDTO(c, MAX(id.comboQuantity)) " +
//            "FROM Combo c " +
//            "JOIN InvoiceDetail id ON id.comboId = c.comboId " +
//            "WHERE id.invoice.invoiceId = :invoiceId " +
//            "GROUP BY c.comboId")
//    List<ComboInvoiceDTO> findCombosWithUniqueQuantityByInvoiceId(@Param("invoiceId") String invoiceId);

}
