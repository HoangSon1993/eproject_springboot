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
    @EmbeddedId
    InvoiceDetailId id;
    Integer quantity;
    BigDecimal uniquePrice;
    int comboQuantity;
    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    Product product;

    @ManyToOne
    @MapsId("invoiceId")
    @JoinColumn(name = "invoice_id", insertable = false, updatable = false)
    Invoice invoice;

    String comboId;
}
