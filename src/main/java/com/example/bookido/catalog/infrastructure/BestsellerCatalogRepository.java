package com.example.bookido.catalog.infrastructure;

import com.example.bookido.catalog.domain.Book;
import com.example.bookido.catalog.domain.CatalogRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
class BestsellerCatalogRepository implements CatalogRepository {
    private final Map<Long, Book> storage = new ConcurrentHashMap<>();

    public BestsellerCatalogRepository() {
        storage.put(1L, new Book(1L, "Kukuczka", "Dariusz Kortko", 2016));
        storage.put(2L, new Book(2L, "Cień Wiatru", "Carlos Luiz Zafón", 2005));
        storage.put(3L, new Book(3L, "Java", "Cay S. Horstmann", 2016));
        storage.put(4L, new Book(4L, "Wzorce Projektowe", "Eric Freeman", 2021));
        storage.put(5L, new Book(5L, "Harry Potter i Komnata Tajemnic", "J. K. Rowling", 1998));
        storage.put(6L, new Book(6L, "Ostatnie Zyczenie", "Andrzej Sapkowski", 2014));
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(storage.values());
    }
}
