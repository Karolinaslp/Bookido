package com.example.bookido;

import com.example.bookido.catalog.application.CatalogService;
import com.example.bookido.catalog.application.port.CatalogUseCase;
import com.example.bookido.catalog.domain.Book;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationStartup implements CommandLineRunner {
    private final CatalogUseCase catalog;
    private final String title;

    public ApplicationStartup(
            CatalogService catalogController,
            @Value("${bookido.catalog.query:Java}") String title
    ) {
        this.catalog = catalogController;
        this.title = title;
    }

    @Override
    public void run(String... args) {
        List<Book> bookList = catalog.findByTitle(title);
        bookList.forEach(System.out::println);
    }
}