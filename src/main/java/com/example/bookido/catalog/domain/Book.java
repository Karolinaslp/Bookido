package com.example.bookido.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = "authors")
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
    @JsonIgnoreProperties("books")
    private Set<Author> authors = new HashSet<>();

    public Book(String title, Integer year, BigDecimal price) {
        this.title = title;

        this.year = year;
        this.price = price;
    }

    public void addAuthor(Author author) {
        authors.add(author);
        author.getBooks().add(this);
    }

    public void removeAuthor(Author author) {
        authors.remove(author);
        author.getBooks().remove(this);
    }

    public void removeAuthors() {
        authors.forEach(author -> author.getBooks().remove(this));
        authors.clear();
    }
}
