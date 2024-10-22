package com.sontung.eproject_springboot.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
    @DecimalMin(
            value = "10000.0",
            inclusive = true,
            message = "Giá phải lớn hơn 0 hoặc bằng 10.000d") // inclusive = true: lớn hơn hoặc bằng
    BigDecimal price;

    Integer status;
    String image;
    LocalDate createdDate;
    LocalDate updatedDate;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<ComboDetail> comboDetails;

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
            return "https://images-xoi-che-co-luyen.s3.ap-southeast-1.amazonaws.com/no-image.jpg";
        }
        return "https://images-xoi-che-co-luyen.s3.ap-southeast-1.amazonaws.com/" + image;
    }
}
