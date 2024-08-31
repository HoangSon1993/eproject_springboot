package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.dto.ComboOrderDTO;
import com.sontung.eproject_springboot.entity.InvoiceDetail;
import com.sontung.eproject_springboot.entity.OrderDetail;
import com.sontung.eproject_springboot.repository.IOrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailService {
    @Autowired
    IOrderDetailRepository iOrderDetailRepository;
    public List<OrderDetail> getOrderDetails(String orderId){
        return iOrderDetailRepository.findByOrderOrderId(orderId);
    }

    public OrderDetail getOrderDetail(String orderId){
        return iOrderDetailRepository.findOrderDetailByOrderOrderId(orderId);
    }

    public List<OrderDetail> getOrderDetails(String orderId, String comboId){
        return iOrderDetailRepository.findByOrderOrderId(orderId);
    }

    //    public List<Combo> getCombos(String invoiceId){
//        return iInvoiceDetailRepository.findCombosByInvoiceId(invoiceId);
//    }
//    public List<ComboOrderDTO> getCombos(String orderId){
//        return iOrderDetailRepository.findCombosWithUniqueQuantityByOrderId(orderId);
//    }
}
