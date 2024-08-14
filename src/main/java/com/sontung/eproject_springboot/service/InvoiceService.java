package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.repository.IInvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {
    @Autowired
    IInvoiceRepository iInvoiceRepository;

}
