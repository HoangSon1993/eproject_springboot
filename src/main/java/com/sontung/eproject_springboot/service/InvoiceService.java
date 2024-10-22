package com.sontung.eproject_springboot.service;

import java.util.List;

import com.sontung.eproject_springboot.entity.Invoice;

public interface InvoiceService {
    List<Invoice> getInvoices();

    Invoice getInvoice(String orderId);
}
