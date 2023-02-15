package com.example.bookido.catalog.web;

import com.example.bookido.catalog.application.port.CatalogUseCase;
import com.example.bookido.catalog.application.port.CatalogUseCase.CreateBookCommand;
import com.example.bookido.catalog.db.AuthorJpaRepository;
import com.example.bookido.catalog.domain.Author;
import com.example.bookido.catalog.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class CatalogControllerIT {

    @Autowired
    AuthorJpaRepository authorJpaRepository;

    @Autowired
    CatalogUseCase catalogUseCase;

    @Autowired
    CatalogController controller;

    @Test
    public void getAllBooks() {
        //given
        Author goetz = authorJpaRepository.save(new Author("Brian Goetz"));
        givenEffectiveJava();
        catalogUseCase.addBook(new CreateBookCommand(
                "Java Concurrency in Practice",
                Set.of(goetz.getId()),
                2006,
                new BigDecimal("129.90"),
                50L
        ));

        //when
        List<Book> all = controller.getAll(Optional.empty(), Optional.empty());
        //then

        assertEquals(2, all.size());
    }

    @Test
    public void getBooksByAuthor() {
        //given
        givenEffectiveJava();
        givenJavConcurrencyInPractice();

        //when
        List<Book> all = controller.getAll(Optional.empty(), Optional.of("Bloch"));
        //then

        assertEquals(1, all.size());
        assertEquals("Effective Java", all.get(0).getTitle());
    }

    @Test
    public void getBooksByTitle() {
        //given
        givenEffectiveJava();
        givenJavConcurrencyInPractice();

        //when
        List<Book> all = controller.getAll(Optional.of("Java Concurrency in Practice"), Optional.empty());

        //then
        assertEquals(1, all.size());
        assertEquals("Java Concurrency in Practice", all.get(0).getTitle());
    }

    private void givenEffectiveJava() {
        Author bloch = authorJpaRepository.save(new Author("Joshua Bloch"));
        catalogUseCase.addBook(new CreateBookCommand(
                "Effective Java",
                Set.of(bloch.getId()),
                2005,
                new BigDecimal("99.90"),
                50L
        ));
    }

    private void givenJavConcurrencyInPractice() {
        Author goetz = authorJpaRepository.save(new Author("Brian Goetz"));
        catalogUseCase.addBook(new CreateBookCommand(
                "Java Concurrency in Practice",
                Set.of(goetz.getId()),
                2006,
                new BigDecimal("129.90"),
                50L
        ));
    }
}