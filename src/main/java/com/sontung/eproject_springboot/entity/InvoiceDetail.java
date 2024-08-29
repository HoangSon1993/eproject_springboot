package com.sontung.eproject_springboot.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

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
    Integer quantity; // so luong
    BigDecimal price; // gia cua 1 san pham
    String productId; // product
    BigDecimal totalPrice; // tong tien cua san pham = quantity * price

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    Invoice invoice;
}
