package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.entity.Invoice;

import java.util.List;

public interface InvoiceService {
    List<Invoice> getInvoices();

    Invoice getInvoice(String orderId);
}
