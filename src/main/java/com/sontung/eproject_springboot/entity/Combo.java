package com.sontung.eproject_springboot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

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
}
