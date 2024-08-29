package com.sontung.eproject_springboot.repository;

import com.sontung.eproject_springboot.entity.InvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IInvoiceDetailRepository extends JpaRepository<InvoiceDetail, String> {
}
