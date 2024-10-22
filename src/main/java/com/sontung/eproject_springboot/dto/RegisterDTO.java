package com.sontung.eproject_springboot.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
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
    List<String> roles;
}
