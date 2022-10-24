package com.example.bookido.order.domain;

import com.example.bookido.catalog.domain.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MemoryOrderRepository implements OrderRepository {
    private final Map<Long, Order> storage = new ConcurrentHashMap<>();
    private final AtomicLong ID_NEXT_VALUE = new AtomicLong(0L);

    @Override
    public Order save(Order order) {
        if (order.getId() != null) {
            storage.put(order.getId(), order);
        } else {
            final long nextId = nextId();
            order.setId(nextId);
            storage.put(nextId, order);
        }

        return order;
    }

    private long nextId() {
        return ID_NEXT_VALUE.getAndIncrement();
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(storage.values());
    }
}
