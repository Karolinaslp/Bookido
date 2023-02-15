package com.example.bookido.order.application.price;

import com.example.bookido.catalog.domain.Book;
import com.example.bookido.order.domain.Order;
import com.example.bookido.order.domain.OrderItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriceServiceTest {

    PriceService priceService = new PriceService();

    @Test
    public void calculateTotalPriceOfEmptyOrder() {
        // Given
        Order order = Order
                .builder()
                .build();

        // When
        OrderPrice price = priceService.calculatePrice(order);

        // Then
        assertEquals(BigDecimal.ZERO, price.finalPrice());
    }

    @Test
    public void calculateTotalPrice() {
        //given
        Book book1 = new Book();
        book1.setPrice(new BigDecimal("12.50"));
        Book book2 = new Book();
        book2.setPrice(new BigDecimal("33.99"));

        Order order = Order
                .builder()
                .item(new OrderItem(book1, 2))
                .item(new OrderItem(book2, 5))
                .build();

        //when
        OrderPrice price = priceService.calculatePrice(order);

        //then
        assertEquals(new BigDecimal("194.95"), price.finalPrice());
        assertEquals(new BigDecimal("194.95"), price.getItemPrice());
    }
}