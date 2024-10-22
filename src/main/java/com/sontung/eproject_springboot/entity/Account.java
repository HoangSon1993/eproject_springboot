package com.sontung.eproject_springboot.entity;

import java.time.LocalDate;
import java.util.List;
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
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "account_id")
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

    @OneToMany(mappedBy = "account")
    List<Cart> carts;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "accounts_roles",
            joinColumns = @JoinColumn(name = "account_id", referencedColumnName = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"))
    private Set<Role> roles;

    @Column(nullable = false)
    @Builder.Default
    private boolean passwordChanged = false; // Thêm trường này
}
