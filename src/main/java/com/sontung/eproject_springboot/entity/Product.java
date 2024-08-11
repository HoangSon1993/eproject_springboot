package com.sontung.eproject_springboot.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String productId;

    @Column(length = 100)
    @NotBlank(message = "Tên sản phẩm không được để trống")
    String productName;

    @Lob
    @NotBlank(message = "Mô tả không được để trống")
    String description;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    BigDecimal price;
    Integer status;
    String image;
    LocalDate createdDate;
    LocalDate updatedDate;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JsonIgnore // Để tránh vòng lặp tuần tự hóa
//    Set<Cart> carts;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<ComboDetail> comboDetails;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<InvoiceDetail> invoiceDetails;

    @PrePersist
    protected void onCreate() {
        var date = LocalDate.now();
        if (createdDate == null) {
            createdDate = date;
        }
        if (updatedDate == null) {
            updatedDate = date;
        }
        if (status == null) {
            status = 1; // Đặt giá trị mặc định cho status
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDate.now();
    }

    @Transient
    public String getImageUrl() {
        if (image == null || image.isEmpty()) {
            return "";
        }
        return "https://images-xoi-che-co-luyen.s3.ap-southeast-1.amazonaws.com/" + image;
    }

}
