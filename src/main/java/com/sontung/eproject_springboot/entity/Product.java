package com.sontung.eproject_springboot.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String productId;
    @Column(length = 100)
    String product_name;
    @Lob
    String description;
    BigDecimal price;
    Integer status;
    String image;
    LocalDate createdDate;
    LocalDate updatedDate;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @OneToOne(mappedBy = "product")
    Cart cart;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<ComboDetail> comboDetails;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<InvoiceDetail> invoiceDetails;

}
