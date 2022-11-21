package com.example.bookido.catalog.web;

import com.example.bookido.catalog.application.port.CatalogUseCase;
import com.example.bookido.catalog.domain.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.net.URI;
import java.util.*;

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

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateBook(@PathVariable Long id, @RequestBody RestBookCommand command) {
        UpdateBookResponse response = catalog.updateBook(command.toUpdateBookCommand(id));
        if (!response.isSuccess()) {
            String message = String.join(", ", response.getErrors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> addBook(@Valid @RequestBody RestBookCommand command) {
        Book book = catalog.addBook(command.toCreateCommand());
        return ResponseEntity.created(createdBookUri(book)).build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        catalog.removeById(id);
    }

    private URI createdBookUri(Book book) {
        return ServletUriComponentsBuilder.fromCurrentRequestUri().path("/" + book.getId().toString()).build().toUri();
    }

    @Data
    private static class RestBookCommand {
        @NotBlank(message = "Please provide a title")
        private String title;

        @NotBlank(message = "Please provide an author")
        private String author;

        @NotNull
        private Integer year;

        @NotNull
        @DecimalMin("0.00")
        private BigDecimal price;

        CreateBookCommand toCreateCommand() {
            return new CreateBookCommand(title, author, year, price);
        }

        UpdateBookCommand toUpdateBookCommand(Long id) {
            return new UpdateBookCommand(id, title, author, year, price);
        }
    }
}
