package com.sontung.eproject_springboot.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String accountId;
    @Column(length = 100)
    String fullName;
    @Column(length = 100)
    String userName;
    String password;
    @Column(length = 150)
    String email;
    @Column(length = 20)
    String phone;
    @Column(length = 200)
    String address;
    String avatar;
    LocalDate dob;

    @OneToMany (mappedBy = "account")
    List<Cart> carts;
}
