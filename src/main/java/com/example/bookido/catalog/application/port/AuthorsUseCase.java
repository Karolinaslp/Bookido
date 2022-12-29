package com.example.bookido.catalog.application.port;

import com.example.bookido.catalog.domain.Author;

import java.util.List;

public interface AuthorsUseCase {
    List<Author> findAll();
}
