package com.example.bookido;

import com.example.bookido.catalog.application.CatalogService;
import com.example.bookido.catalog.application.port.CatalogUseCase;
import com.example.bookido.catalog.application.port.CatalogUseCase.UpdateBookCommand;
import com.example.bookido.catalog.application.port.CatalogUseCase.UpdateBookResponse;
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
        initData();
        findByTitle();
        findAndUpdate();
        findByTitle();
    }

    private void initData() {
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Granica", "Zofia Nałkowska", 1835));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Hamlet", "William Szekspir", 1602));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Chłopi", "Władysław Reymont", 1908));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Pan Wołodyjowski", "Henryk Sienkiewicz", 1888));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Kukuczka", "Dariusz Kortko", 2016));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Cień Wiatru", "Carlos Luiz Zafón", 2005));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Java", "Cay S. Horstmann", 2016));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Wzorce Projektowe", "Eric Freeman", 2021));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Harry Potter i Komnata Tajemnic", "J. K. Rowling", 1998));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Ostatnie Zyczenie", "Andrzej Sapkowski", 2014));
    }

    private void findByTitle() {
        List<Book> bookList = catalog.findByTitle(title);
        bookList.forEach(System.out::println);
    }

    private void findAndUpdate() {
        System.out.println("Updating book...");
        catalog.findOneByTitleAndAuthor("Harry", "J. K. Rowling")
                .ifPresent(book -> {
                    final UpdateBookCommand command = UpdateBookCommand
                            .builder()
                            .id(book.getId())
                            .title("Harry Potter i Komnata Tajemnic Xdd")
                            .build();
                    UpdateBookResponse response = catalog.updateBook(command);
                    System.out.println("Updating book result: " + response.isSuccess());
                });

    }
}