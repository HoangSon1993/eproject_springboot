package com.sontung.eproject_springboot.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

import com.sontung.eproject_springboot.enums.InvoiceStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String invoiceId; // invoiceNumber

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime invoiceDate; // Ngày hóa đơn dược lạp

    BigDecimal totalAmount; // Tổng tiền

    /**
     * Trong DB sẽ lưu giá trị  0,1,2,3
     * UNPAID:  'Chưa thanh toán'
     * PAID:    'Đã thanh toán'
     * PENDING: 'Đang chờ xử lý'
     * FAILED:   'Thanh toán thất bại'
     **/
    @Enumerated(EnumType.ORDINAL)
    InvoiceStatus paymentStatus; // Trạng thái thanh toán  (đã thanh toán, chưa thanh toán, hủy bỏ)

    String paymentMethod; // Phương thức thanh toán (chuyển khoản, tín dụng, tiền mặt)
    LocalDateTime paymentDate; // Ngày thanh toán

    String bankCode; // Mã ngân hàng
    String bankTransactionNo; // Mã giao dịch ngân hàng
    String cardType; // Loại thẻ (ATM, tín dụng, etc.)
    String transactionStatus; // Trạng thái giao dịch từ VNPay
    String transactionNo; // Mã giao dịch VNPay

    @OneToOne
    @JoinColumn(name = "order_id", unique = true) // unique to enforce the one-to-one relationship
    Order order;

    /**
     * OneToMay: 'Invoice' (1 - n) 'InvoiceDetail'
     * param: mappedBy: Mối quan hệ này được quản lý bởi thuộc tính 'invoice' trong 'InvoiceDetail'
     * param :orphanRemoval: Nếu 1 'invoice_detail' bị xóa khỏi ds 'invoiceDetails', nó cũng bị xóa khỏi CSDL
     **/
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<InvoiceDetail> invoiceDetails = new ArrayList<>();

    // Add widget to add invoice details to invoice.
    public void addInvoiceDetail(InvoiceDetail invoiceDetail) {
        invoiceDetails.add(invoiceDetail);
        invoiceDetail.setInvoice(this);
    }

    public void removeInvoiceDetail(InvoiceDetail invoiceDetail) {
        invoiceDetails.remove(invoiceDetail);
        invoiceDetail.setInvoice(null);
    }
}
