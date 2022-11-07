package com.example.bookido.catalog.web;

import com.example.bookido.catalog.application.port.CatalogUseCase;
import com.example.bookido.catalog.domain.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.example.bookido.catalog.application.port.CatalogUseCase.*;

@RequestMapping("catalog")
@RestController
@AllArgsConstructor
class CatalogController {
    private final CatalogUseCase catalog;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Book> getAll(
            @RequestParam Optional<String> title,
            @RequestParam Optional<String> author
    ) {
        if (title.isPresent() && author.isPresent()) {
            return catalog.findByTitleAndAuthor(title.get(), author.get());
        } else if (title.isPresent()) {
            return catalog.findByTitle(title.get());
        } else if (author.isPresent()) {
            return catalog.findByAuthor(author.get());
        }
        return catalog.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return catalog
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public void addBook(@RequestBody RestCreateBookCommand command) {
        catalog.addBook(command.toCommand());
    }

    @Data
    private static class RestCreateBookCommand {
        private String title;
        private String author;
        private Integer year;
        private BigDecimal price;

        CreateBookCommand toCommand() {
            return new CreateBookCommand(title, author, year, price);

        }
    }
}
