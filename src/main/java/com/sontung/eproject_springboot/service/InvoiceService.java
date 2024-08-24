package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.dto.ComboInvoiceDTO;
import com.sontung.eproject_springboot.dto.InvoiceDetailDTO;
import com.sontung.eproject_springboot.entity.Invoice;
import com.sontung.eproject_springboot.entity.InvoiceDetail;
import com.sontung.eproject_springboot.repository.IComboRepository;
import com.sontung.eproject_springboot.repository.IInvoiceRepository;
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
    InvoiceDetailService invoiceDetailService;
    @Autowired
    IComboRepository iComboRepository;
    public List<Invoice> getInvoices(){
        return iInvoiceRepository.findAll();
    }
    public Invoice getInvoice(String invoiceId){
        return iInvoiceRepository.findById(invoiceId).orElseThrow(()-> new RuntimeException("Không tìm thấy hóa đơn"));
    }
    public List<InvoiceDetailDTO> getInvoiceDetails(String invoiceId){
        List<InvoiceDetail> invoiceDetails = invoiceDetailService.getInvoiceDetails(invoiceId);
        List<InvoiceDetailDTO> invoiceDetailsDTOList = new ArrayList<>();
        for (InvoiceDetail item: invoiceDetails) {
            InvoiceDetailDTO invoiceDetailDTO = new InvoiceDetailDTO();
            invoiceDetailDTO.setInvoice(item.getInvoice());
            invoiceDetailDTO.setProduct(item.getProduct());
            invoiceDetailDTO.setQuantity(item.getQuantity());
            invoiceDetailDTO.setUniquePrice(item.getUniquePrice());
            invoiceDetailDTO.setComboQuantity(item.getComboQuantity());
            if((item.getComboId()).equals("Sản phẩm lẻ")){
                invoiceDetailsDTOList.add(invoiceDetailDTO);
            }
            else{
                invoiceDetailDTO.setCombo(iComboRepository.findById(item.getComboId()).orElseThrow(()-> new RuntimeException("Không tìm thấy combo")));
                invoiceDetailsDTOList.add(invoiceDetailDTO);
            }
        }
        return invoiceDetailsDTOList;
    }
//    public List<Combo> getCombosInvoice(String invoiceId){
//        return invoiceDetailService.getCombos(invoiceId);
//    }
    public List<ComboInvoiceDTO> getCombosInvoice(String invoiceId){
        return invoiceDetailService.getCombos(invoiceId);
    }

    public List<Invoice> getInvoicesByPrice(int priceValue) {
        List<Invoice> invoices = new ArrayList<>();

        switch (priceValue) {
            case 1: // Dưới 100k
                invoices = iInvoiceRepository.findAll().stream()
                        .filter(i -> i.getTotalAmount().compareTo(new BigDecimal("100000")) < 0)
                        .collect(Collectors.toList());
                break;
            case 2: // 100k -> 200k
                invoices = iInvoiceRepository.findAll().stream()
                        .filter(i -> i.getTotalAmount().compareTo(new BigDecimal("100000")) >= 0 &&
                                i.getTotalAmount().compareTo(new BigDecimal("200000")) <= 0)
                        .collect(Collectors.toList());
                break;
            case 3: // 200k -> 300k
                invoices = iInvoiceRepository.findAll().stream()
                        .filter(i -> i.getTotalAmount().compareTo(new BigDecimal("200000")) > 0 &&
                                i.getTotalAmount().compareTo(new BigDecimal("300000")) <= 0)
                        .collect(Collectors.toList());
                break;
            case 4: // 300k -> 500k
                invoices = iInvoiceRepository.findAll().stream()
                        .filter(i -> i.getTotalAmount().compareTo(new BigDecimal("300000")) > 0 &&
                                i.getTotalAmount().compareTo(new BigDecimal("500000")) <= 0)
                        .collect(Collectors.toList());
                break;
            case 5: // 500k Trở Lên
                invoices = iInvoiceRepository.findAll().stream()
                        .filter(i -> i.getTotalAmount().compareTo(new BigDecimal("500000")) > 0)
                        .collect(Collectors.toList());
                break;
            default:
                throw new IllegalArgumentException("Invalid price range");
        }
        return invoices;
    }
    public List<Invoice> getInvoicesByFilterDate(@DateTimeFormat(pattern = "yyyy-MM-dd") Date filterDate){
        LocalDate filterLocalDate = filterDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return iInvoiceRepository.findAll().stream().filter(i->i.getCreatedDate().equals(filterLocalDate)).collect(Collectors.toList());
    }

    public List<Invoice> getInvoicesByPriceAndDate(int priceValue,
                                                   @DateTimeFormat(pattern = "yyyy-MM-dd") Date filterDate){
        LocalDate filterLocalDate = filterDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return iInvoiceRepository.findAll().stream().filter(i->{
            BigDecimal totalAmount = i.getTotalAmount();
            boolean priceMatches = false;
            switch (priceValue) {
                case 1: // Dưới 100k
                    priceMatches = totalAmount.compareTo(new BigDecimal("100000")) < 0;
                    break;
                case 2: // 100k -> 200k
                    priceMatches = totalAmount.compareTo(new BigDecimal("100000")) >= 0 &&
                            totalAmount.compareTo(new BigDecimal("200000")) <= 0;
                    break;
                case 3: // 200k -> 300k
                    priceMatches = totalAmount.compareTo(new BigDecimal("200000")) > 0 &&
                            totalAmount.compareTo(new BigDecimal("300000")) <= 0;
                    break;
                case 4: // 300k -> 500k
                    priceMatches = totalAmount.compareTo(new BigDecimal("300000")) > 0 &&
                            totalAmount.compareTo(new BigDecimal("500000")) <= 0;
                    break;
                case 5: // 500k Trở Lên
                    priceMatches = totalAmount.compareTo(new BigDecimal("500000")) > 0;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid price range");
            }
            return priceMatches && i.getCreatedDate().equals(filterLocalDate);
        }).collect(Collectors.toList());
    }
}
