package com.example.bookido.order.applocation;

import com.example.bookido.order.applocation.port.QueryOrderUseCase;
import com.example.bookido.order.domain.Order;
import com.example.bookido.order.domain.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class QueryOrderService implements QueryOrderUseCase {
   private final OrderRepository repository;

    @Override
    public List<Order> findAll() {
        return repository.findAll();
    }
}
