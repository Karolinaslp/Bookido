package com.example.bookido.catalog.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class Book {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private Integer year;
    private BigDecimal price;
    private Long coverId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable
    private Set<Author> authors;

    public Book(String title, Integer year, BigDecimal price) {
        this.title = title;

        this.year = year;
        this.price = price;
    }
}
