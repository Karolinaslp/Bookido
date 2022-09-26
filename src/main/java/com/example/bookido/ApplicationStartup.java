package com.example.bookido;

import com.example.bookido.catalog.application.CatalogController;
import com.example.bookido.catalog.domain.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ApplicationStartup implements CommandLineRunner {
    private final CatalogController catalogController;

    @Override
    public void run(String... args) {
        List<Book> bookList = catalogController.findByTitle("Java");
        bookList.forEach(System.out::println);
    }
}