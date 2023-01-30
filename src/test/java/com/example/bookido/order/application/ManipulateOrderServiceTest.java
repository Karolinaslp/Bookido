package com.example.bookido.order.application;

import com.example.bookido.catalog.db.BookJpaRepository;
import com.example.bookido.catalog.domain.Book;
import com.example.bookido.order.domain.Recipient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static com.example.bookido.order.application.port.ManipulateOrderUseCase.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({ManipulateOrderService.class})
class ManipulateOrderServiceTest {
    @Autowired
    BookJpaRepository bookRepository;

    @Autowired
    ManipulateOrderService service;
    @Test
    public void userCanPlaceOrder() {
        //Given
        Book effectiveJava = givenEffectiveJava(50L);
        Book javaConcurrency = givenJavaConcurrency(50L);
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(effectiveJava.getId(), 10))
                .item(new OrderItemCommand(javaConcurrency.getId(), 10))
                .build();
        //When
        PlaceOrderResponse response = service.placeOrder(command);
        //Then
        assertTrue(response.isSuccess());
    }

    private Recipient recipient() {
        return Recipient.builder().email("john@example.org").build();
    }

    private Book givenJavaConcurrency(long available) {
        return bookRepository.save(new Book("Java Concurrency in Practice", 2006, new BigDecimal("99.90"), available));
    }

    private Book givenEffectiveJava(long available) {
        return bookRepository.save(new Book("Effective java", 2005, new BigDecimal("199.90"), available));
    }
}