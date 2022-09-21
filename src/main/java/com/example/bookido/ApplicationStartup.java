package com.example.bookido;

import com.example.bookido.catalog.application.CatalogController;
import com.example.bookido.catalog.domain.Book;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationStartup implements CommandLineRunner {
    private final CatalogController catalogController;

    public ApplicationStartup(CatalogController catalogController) {
        this.catalogController = catalogController;
    }

    @Override
    public void run(String... args) {
        List<Book> bookList = catalogController.findByTitle("Pan");
        bookList.forEach(System.out::println);
    }
}