package com.sontung.eproject_springboot.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

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
public class Combo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String comboId;

    @Column(length = 100)
    String comboName;

    String image;
    String description;
    Integer status;
    BigDecimal totalAmount;
    BigDecimal finalAmount;
    LocalDate createdDate;
    LocalDate updatedDate;
    LocalDate startDate;
    LocalDate endDate;

    @OneToMany(mappedBy = "combo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<ComboDetail> comboDetails;

    @Transient
    public String getImageUrl() {
        if (image == null || image.isEmpty()) {
            return "";
        }
        return "https://images-xoi-che-co-luyen.s3.ap-southeast-1.amazonaws.com/" + image;
    }
}
