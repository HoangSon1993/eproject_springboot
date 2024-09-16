package com.sontung.eproject_springboot.dto;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
public class RegisterDTO {
    String fullName;
    String userName;
    String password;
    String passwordConfirm;
    String email;
    String phone;
    String address;
    String avatar;
    LocalDate dob;
}
