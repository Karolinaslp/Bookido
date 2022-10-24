package com.example.bookido.order.domain;

import com.example.bookido.catalog.domain.Book;
import lombok.Value;

@Value
public class OrderItem {
    Book book;
    int quantity;
}
