package com.example.bookido.catalog.infrastructure;

import com.example.bookido.catalog.domain.Book;
import com.example.bookido.catalog.domain.CatalogRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
class MemoryCatalogRepository implements CatalogRepository {
    private final Map<Long, Book> storage = new ConcurrentHashMap<>();
    private final AtomicLong ID_NEXT_VALUE = new AtomicLong(0L);
    
    @Override
    public List<Book> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void save(Book book) {
        final long nextId = nextId();
        book.setId(nextId);
        storage.put(nextId, book);
    }

    private long nextId() {
        return ID_NEXT_VALUE.getAndIncrement();
    }
}
