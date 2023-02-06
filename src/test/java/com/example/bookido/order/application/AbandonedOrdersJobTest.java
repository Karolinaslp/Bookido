package com.example.bookido.order.application;

import com.example.bookido.catalog.application.port.CatalogUseCase;
import com.example.bookido.catalog.db.BookJpaRepository;
import com.example.bookido.catalog.domain.Book;
import com.example.bookido.clock.Clock;
import com.example.bookido.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import com.example.bookido.order.application.port.QueryOrderUseCase;
import com.example.bookido.order.domain.OrderStatus;
import com.example.bookido.order.domain.Recipient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.Duration;

import static com.example.bookido.order.application.port.ManipulateOrderUseCase.OrderItemCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties ="app.orders.payment-period=1H")
@AutoConfigureTestDatabase
class AbandonedOrdersJobTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public Clock.Fake clock() {
            return new Clock.Fake();
        }
    }

    @Autowired
    AbandonedOrdersJob ordersJob;
    @Autowired
    ManipulateOrderService manipulateOrderService;
    @Autowired
    BookJpaRepository bookRepository;
    @Autowired
    QueryOrderUseCase queryOrderService;
    @Autowired
    CatalogUseCase catalogUseCase;
    @Autowired
    Clock.Fake clock;

    @Test
    public void shouldMarkOrdersAsAbandoned() {
        // Given - orders
        Book book = givenEffectiveJava(50L);
        Long orderId = placeOrder(book.getId(), 15);

        // When - run
        clock.tick(Duration.ofHours(2));
        ordersJob.run();

        // Then - status changed
        assertEquals(OrderStatus.ABANDONED, queryOrderService.findById(orderId).get().getStatus());
        assertEquals(50L, availableCopiesOf(book));
    }

    private Long placeOrder(Long bookId, int copies) {
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(bookId, copies))
                .build();
        return manipulateOrderService.placeOrder(command).getRight();
    }

    private Recipient recipient() {
        return Recipient.builder().email("marek@example.org").build();
    }

    private Book givenJavaConcurrency(long available) {
        return bookRepository.save(new Book("Java Concurrency in Practice", 2006, new BigDecimal("99.90"), available));
    }

    private Book givenEffectiveJava(long available) {
        return bookRepository.save(new Book("Effective java", 2005, new BigDecimal("199.90"), available));
    }

    private Long availableCopiesOf(Book effectiveJava) {
        return catalogUseCase.findById(effectiveJava.getId())
                .get()
                .getAvailable();
    }
}
