package com.sontung.eproject_springboot.service.impl;

import com.sontung.eproject_springboot.entity.OrderDetail;
import com.sontung.eproject_springboot.repository.IOrderDetailRepository;
import com.sontung.eproject_springboot.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImp implements OrderDetailService {
    private final IOrderDetailRepository iOrderDetailRepository;

    @Override
    public List<OrderDetail> getOrderDetails(String orderId) {
        return iOrderDetailRepository.findByOrderOrderId(orderId);
    }

    @Override
    public List<OrderDetail> getOrderDetails(String orderId, String comboId) {
        return iOrderDetailRepository.findByOrderOrderId(orderId);
    }

    //    public List<Combo> getCombos(String invoiceId){
//        return iInvoiceDetailRepository.findCombosByInvoiceId(invoiceId);
//    }
//    public List<ComboOrderDTO> getCombos(String orderId){
//        return iOrderDetailRepository.findCombosWithUniqueQuantityByOrderId(orderId);
//    }
}
