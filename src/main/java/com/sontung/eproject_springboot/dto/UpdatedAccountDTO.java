package com.sontung.eproject_springboot.dto;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatedAccountDTO {
    String userName;
    String fullName;
    String email;
    String phone;
    String address;
    String avatar;
    LocalDate dob;
}
