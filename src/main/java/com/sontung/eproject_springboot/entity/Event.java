package com.sontung.eproject_springboot.entity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String eventId;

    @Column(length = 4000)
    String content;

    String image;

    @Column(length = 4000)
    String description;
}
