package com.example.bookido.order.application;

import com.example.bookido.catalog.domain.Book;
import com.example.bookido.order.domain.OrderItem;
import com.example.bookido.order.domain.OrderStatus;
import com.example.bookido.order.domain.Recipient;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RichOrderTest {
    @Test
    public void calculateTotalPriceOfEmptyOrder() {
        //given
        RichOrder order = new RichOrder(
                1L,
                OrderStatus.NEW,
                Collections.emptySet(),
                Recipient.builder().build(),
                LocalDateTime.now()
                );

        //when
        BigDecimal price = order.totalPrice();

        //then
        assertEquals(BigDecimal.ZERO, price);
    }

    @Test
    public void calculateTotalPrice() {
        //given
        Book book1 = new Book();
        book1.setPrice(new BigDecimal("12.50"));
        Book book2 = new Book();
        book2.setPrice(new BigDecimal("33.99"));
        Set<OrderItem> items = new HashSet<>(
                Arrays.asList(
                        new OrderItem(book1, 2),
                        new OrderItem(book2, 5)
                )
        );
        RichOrder order = new RichOrder(
                1L,
                OrderStatus.NEW,
                items,
                Recipient.builder().build(),
                LocalDateTime.now()
        );

        //when
        BigDecimal price = order.totalPrice();

        //then
        assertEquals(new BigDecimal("194.95"), price);
    }
}