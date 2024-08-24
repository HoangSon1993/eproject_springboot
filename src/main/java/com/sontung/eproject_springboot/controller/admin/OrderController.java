package com.sontung.eproject_springboot.controller.admin;

import com.sontung.eproject_springboot.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
@RequestMapping("/admin/order")
public class OrderController {

    @Autowired
    InvoiceService invoiceService;

    @Value("${aws.s3.bucket.url}")
    String s3BucketUrl;

    @ModelAttribute("s3BucketUrl")
    public String s3BucketUrl() {
        return s3BucketUrl;
    }
    @GetMapping
    public String getInvoices(@RequestParam(required = false, defaultValue = "0") int amongPrice,
                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date filterDate,
                              Model model) {
        if (amongPrice == 0 && filterDate==null) {
            model.addAttribute("invoices", invoiceService.getInvoices());
        } else if(amongPrice == 0 && filterDate !=null){
            model.addAttribute("filterDate", filterDate);
            model.addAttribute("invoices", invoiceService.getInvoicesByFilterDate(filterDate));
        } else {
            if(filterDate==null){
                model.addAttribute("invoices", invoiceService.getInvoicesByPrice(amongPrice));
                model.addAttribute("amongPrice", amongPrice);
            }
            else{
                model.addAttribute("filterDate", filterDate);
                model.addAttribute("amongPrice", amongPrice);
                model.addAttribute("invoices", invoiceService.getInvoicesByPriceAndDate(amongPrice, filterDate));
            }
        }
        return "/admin/order/index";
    }
    @GetMapping("/detail")
    public String getInvoiceDetails(@RequestParam String invoiceId, Model model){
        model.addAttribute("invoice", invoiceService.getInvoice(invoiceId));
        model.addAttribute("invoiceDetails", invoiceService.getInvoiceDetails(invoiceId));
        model.addAttribute("combos", invoiceService.getCombosInvoice(invoiceId));
        return "/admin/order/detail";
    }
}
