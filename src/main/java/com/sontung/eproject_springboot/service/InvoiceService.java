package com.sontung.eproject_springboot.service;


import com.sontung.eproject_springboot.dto.InvoiceDetailDTO;
import com.sontung.eproject_springboot.entity.Invoice;
import com.sontung.eproject_springboot.entity.InvoiceDetail;
import com.sontung.eproject_springboot.entity.Order;
import com.sontung.eproject_springboot.repository.IComboRepository;
import com.sontung.eproject_springboot.repository.IInvoiceRepository;
import com.sontung.eproject_springboot.repository.IOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {
    @Autowired
    IInvoiceRepository iInvoiceRepository;
    @Autowired
    IOrderRepository iOrderRepository;

    @Autowired
    IComboRepository iComboRepository;
    public List<Invoice> getInvoices(){
        return iInvoiceRepository.findAll();
    }
    public Invoice getInvoice(String orderId){
        Order order = iOrderRepository.findById(orderId).orElseThrow(()-> new RuntimeException("Không tìm thấy đơn hàng"));
        return iInvoiceRepository.findInvoiceByOrder(order);
    }
}
