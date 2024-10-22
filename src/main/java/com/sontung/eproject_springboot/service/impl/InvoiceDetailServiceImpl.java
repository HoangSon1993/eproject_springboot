package com.sontung.eproject_springboot.service.impl;

import org.springframework.stereotype.Service;

import com.sontung.eproject_springboot.repository.IInvoiceDetailRepository;
import com.sontung.eproject_springboot.service.InvoiceDetailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceDetailServiceImpl implements InvoiceDetailService {
    private final IInvoiceDetailRepository invoiceDetailRepository;

    //    public List<InvoiceDetail> getInvoiceDetails(String invoiceId){
    //        return iInvoiceDetailRepository.findByIdInvoiceId(invoiceId);
    //    }
    //
    //    public List<InvoiceDetail> getInvoiceDetails(String invoiceId, String comboId){
    //        return iInvoiceDetailRepository.findByIdInvoiceId(invoiceId);
    //    }
    //
    //    public List<Combo> getCombos(String invoiceId){
    //        return iInvoiceDetailRepository.findCombosByInvoiceId(invoiceId);
    //    }
    //
    //    public List<ComboInvoiceDTO> getCombos(String invoiceId){
    //        return iInvoiceDetailRepository.findCombosWithUniqueQuantityByInvoiceId(invoiceId);
    //    }
}
