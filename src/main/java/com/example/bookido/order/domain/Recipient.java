package com.example.bookido.order.domain;

import com.example.bookido.jpa.BaseEntity;
import lombok.*;

import javax.persistence.Entity;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Recipient extends BaseEntity {
    private String email;
    private String name;
    private String phone;
    private String street;
    private String city;
    private String zipCode;
}
