package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.dto.ComboInvoiceDTO;
import com.sontung.eproject_springboot.entity.Cart;
import com.sontung.eproject_springboot.entity.Combo;
import com.sontung.eproject_springboot.entity.InvoiceDetail;
import com.sontung.eproject_springboot.repository.IInvoiceDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class InvoiceDetailService {
    @Autowired
    IInvoiceDetailRepository iInvoiceDetailRepository;
    public List<InvoiceDetail> getInvoiceDetails(String invoiceId){
        return iInvoiceDetailRepository.findByIdInvoiceId(invoiceId);
    }

    public List<InvoiceDetail> getInvoiceDetails(String invoiceId, String comboId){
        return iInvoiceDetailRepository.findByIdInvoiceId(invoiceId);
    }

//    public List<Combo> getCombos(String invoiceId){
//        return iInvoiceDetailRepository.findCombosByInvoiceId(invoiceId);
//    }
    public List<ComboInvoiceDTO> getCombos(String invoiceId){
        return iInvoiceDetailRepository.findCombosWithUniqueQuantityByInvoiceId(invoiceId);
    }
}
