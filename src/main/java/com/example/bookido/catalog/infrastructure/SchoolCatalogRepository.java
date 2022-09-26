package com.example.bookido.catalog.infrastructure;

import com.example.bookido.catalog.domain.Book;
import com.example.bookido.catalog.domain.CatalogRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
class SchoolCatalogRepository implements CatalogRepository {
    private final Map<Long, Book> storage = new ConcurrentHashMap<>();

    public SchoolCatalogRepository() {
        storage.put(1L, new Book(1L, "Granica", "Zofia Nałkowska", 1835));
        storage.put(2L, new Book(2L, "Hamlet", "William Szekspir", 1602));
        storage.put(3L, new Book(3L, "Chłopi", "Władysław Reymont", 1908));
        storage.put(4L, new Book(4L, "Pan Wołodyjowski", "Henryk Sienkiewicz", 1888));
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(storage.values());
    }
}

