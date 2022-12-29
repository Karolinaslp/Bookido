package com.example.bookido.catalog.application;

import com.example.bookido.catalog.application.port.AuthorsUseCase;
import com.example.bookido.catalog.db.AuthorJpaRepository;
import com.example.bookido.catalog.domain.Author;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthorsService implements AuthorsUseCase {
    private final AuthorJpaRepository repository;
    @Override
    public List<Author> findAll() {
        return repository.findAll();
    }
}
