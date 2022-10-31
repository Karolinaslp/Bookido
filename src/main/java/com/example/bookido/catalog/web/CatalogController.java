package com.example.bookido.catalog.web;

import com.example.bookido.catalog.application.port.CatalogUseCase;
import com.example.bookido.catalog.domain.Book;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RequestMapping("/catalog")
@RestController
@AllArgsConstructor
class CatalogController {
    private final CatalogUseCase catalog;

    @GetMapping
    public List<Book> getAll() {
        return catalog.findAll();
    }
}
