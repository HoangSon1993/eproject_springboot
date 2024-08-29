package com.sontung.eproject_springboot.dto;

import com.sontung.eproject_springboot.entity.Combo;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComboOrderDTO {
    Combo combo;
    int quantity;
}
