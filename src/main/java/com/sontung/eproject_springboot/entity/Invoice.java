package com.sontung.eproject_springboot.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String invoiceId;
    String code;
    LocalDate createdDate;
    String shippingAddress;
    String shippingPhone;
    BigDecimal totalAmount;
    Integer status;
    String accountId;
    String email;
    String fullName;
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<InvoiceDetail> invoiceDetails;
}
