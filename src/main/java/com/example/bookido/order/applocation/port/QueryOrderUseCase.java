package com.example.bookido.order.applocation.port;

import com.example.bookido.order.domain.Order;

import java.util.List;

public interface QueryOrderUseCase {
    List<Order> findAll();
}
