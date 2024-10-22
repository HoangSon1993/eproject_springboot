package com.sontung.eproject_springboot.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String invoiceDetailId;

    Integer quantity; // Số lượng
    BigDecimal price; // Giá của 1 sản phẩm
    String productId; // Product
    String comboId; // Combo
    BigDecimal totalPrice; // Tổng tiền của sản phẩm = quantity * price

    /**
     * @ManyToOne: InvoiceDetail n-1 Invocie.
     * @JoinColumn: Cột này lưu giá trị của khóa chính từ bảng 'invoice'
     * **/
    @ManyToOne
    @JoinColumn(name = "invoice_id")
    Invoice invoice;
}
