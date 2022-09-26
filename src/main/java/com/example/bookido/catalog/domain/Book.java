package com.example.bookido.catalog.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Book {
    Long id;
    String title;
    String author;
    Integer year;
}

