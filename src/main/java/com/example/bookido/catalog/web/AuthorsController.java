package com.example.bookido.catalog.web;

import com.example.bookido.catalog.application.port.AuthorsUseCase;
import com.example.bookido.catalog.domain.Author;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("authors")
public class AuthorsController {
    private final AuthorsUseCase authors;

    @GetMapping
    public List<Author> findAll() {
        return authors.findAll();
    }
}
