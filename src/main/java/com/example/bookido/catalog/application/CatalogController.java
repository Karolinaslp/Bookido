package com.example.bookido.catalog.application;

import com.example.bookido.catalog.domain.Book;
import com.example.bookido.catalog.domain.CatalogService;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class CatalogController {
    private final CatalogService service;

    public CatalogController(CatalogService service) {
        this.service = service;
    }

    public List<Book> findByTitle(String title) {
        return service.findByTitle(title);
    }
}
