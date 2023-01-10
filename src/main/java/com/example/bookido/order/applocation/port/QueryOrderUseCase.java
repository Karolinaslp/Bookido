package com.example.bookido.order.applocation.port;

import com.example.bookido.catalog.domain.Book;
import com.example.bookido.order.domain.Order;
import com.example.bookido.order.domain.OrderItem;
import com.example.bookido.order.domain.OrderStatus;
import com.example.bookido.order.domain.Recipient;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QueryOrderUseCase {
    List<RichOrder> findAll();

    Optional<RichOrder> findById(Long id);

    @Value
    class RichOrder {
        Long id;
        OrderStatus status;
        Set<OrderItem> items;
        Recipient recipient;
        LocalDateTime createdAt;

        public BigDecimal totalPrice() {
            return items.stream()
                    .map(item -> item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }
}
