package com.sontung.eproject_springboot.repository;

import com.sontung.eproject_springboot.entity.Invoice;
import com.sontung.eproject_springboot.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IInvoiceRepository extends JpaRepository<Invoice, String> {
    Invoice findInvoiceByOrder(Order order);
}