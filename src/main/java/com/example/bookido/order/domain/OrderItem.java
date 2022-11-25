package com.example.bookido.order.domain;

import lombok.Value;

@Value
public class OrderItem {
    Long bookId;
    int quantity;
}
