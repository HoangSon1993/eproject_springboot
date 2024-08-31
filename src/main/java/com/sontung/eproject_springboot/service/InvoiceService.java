package com.sontung.eproject_springboot.service;


import com.sontung.eproject_springboot.entity.Invoice;
import com.sontung.eproject_springboot.entity.Order;
import com.sontung.eproject_springboot.repository.IComboRepository;
import com.sontung.eproject_springboot.repository.IInvoiceRepository;
import com.sontung.eproject_springboot.repository.IOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceService {
    private final
    IInvoiceRepository iInvoiceRepository;
    private final
    IOrderRepository iOrderRepository;

    private final
    IComboRepository iComboRepository;

    public InvoiceService(IInvoiceRepository iInvoiceRepository, IOrderRepository iOrderRepository, IComboRepository iComboRepository) {
        this.iInvoiceRepository = iInvoiceRepository;
        this.iOrderRepository = iOrderRepository;
        this.iComboRepository = iComboRepository;
    }

    public List<Invoice> getInvoices(){
        return iInvoiceRepository.findAll();
    }
    public Invoice getInvoice(String orderId){
        Order order = iOrderRepository.findById(orderId).orElseThrow(()-> new RuntimeException("Không tìm thấy đơn hàng"));
        return iInvoiceRepository.findInvoiceByOrder(order);
    }
}