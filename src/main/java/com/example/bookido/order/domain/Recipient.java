package com.example.bookido.order.domain;

import lombok.Value;

@Value
public class Recipient {
    String name;
    String phone;
    String street;
    String city;
    String zipCode;
    String email;
}
