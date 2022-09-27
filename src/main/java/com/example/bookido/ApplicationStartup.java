package com.example.bookido;

import com.example.bookido.catalog.application.CatalogController;
import com.example.bookido.catalog.domain.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component

public class ApplicationStartup implements CommandLineRunner {
    private final CatalogController catalogController;
    private final String title;

    public ApplicationStartup(
            CatalogController catalogController,
            @Value("${bookido.catalog.query:Java}") String title
    ) {
        this.catalogController = catalogController;
        this.title = title;
    }

    @Override
    public void run(String... args) {
        List<Book> bookList = catalogController.findByTitle(title);
        bookList.forEach(System.out::println);
    }
}