package com.example.bookido.catalog.application.port;

import com.example.bookido.catalog.domain.Book;
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.*;

public interface CatalogUseCase {
    List<Book> findByTitle(String title);

    List<Book> findAll();

    Optional<Book> findOneByTitleAndAuthor(String title, String author);

    void addBook(CreateBookCommand command);

    UpdateBookResponse updateBook(UpdateBookCommand command);

    void removeById(Long id);

    @Value
    class CreateBookCommand{
        String title;
        String author;
        Integer year;
    }
    @Value
    class UpdateBookCommand {
        Long id;
        String title;
        String author;
        Integer year;
    }

    @Value
    class UpdateBookResponse {
        public static UpdateBookResponse SUCCESS = new UpdateBookResponse(true, emptyList());
        boolean success;
        List<String> errors;
    }
}
