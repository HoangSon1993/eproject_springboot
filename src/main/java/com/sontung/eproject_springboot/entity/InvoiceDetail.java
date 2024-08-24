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
    Integer quantity;
    BigDecimal price;
    String productId;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    Invoice invoice;


}
