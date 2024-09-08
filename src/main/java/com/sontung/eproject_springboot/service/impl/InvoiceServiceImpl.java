package com.sontung.eproject_springboot.service.impl;

import com.sontung.eproject_springboot.entity.Invoice;
import com.sontung.eproject_springboot.entity.Order;
import com.sontung.eproject_springboot.repository.IComboRepository;
import com.sontung.eproject_springboot.repository.IInvoiceRepository;
import com.sontung.eproject_springboot.repository.IOrderRepository;
import com.sontung.eproject_springboot.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final IInvoiceRepository iInvoiceRepository;
    private final IOrderRepository iOrderRepository;
    private final IComboRepository iComboRepository;

    @Override
    public List<Invoice> getInvoices() {
        return iInvoiceRepository.findAll();
    }

    @Override
    public Invoice getInvoice(String orderId) {
        Order order = iOrderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        return iInvoiceRepository.findInvoiceByOrder(order);
    }
}