package com.sontung.eproject_springboot.controller.admin;

import com.sontung.eproject_springboot.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/invoice")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;

    @GetMapping("/order-invoice")
    public String getInvoice(@RequestParam String orderId, Model model){
        model.addAttribute("invoice", invoiceService.getInvoice(orderId));
        return "/admin/invoice/index";
    }
}
