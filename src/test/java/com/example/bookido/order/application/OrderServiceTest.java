package com.example.bookido.order.application;

import com.example.bookido.catalog.application.port.CatalogUseCase;
import com.example.bookido.catalog.db.BookJpaRepository;
import com.example.bookido.catalog.domain.Book;
import com.example.bookido.order.application.port.QueryOrderUseCase;
import com.example.bookido.order.domain.OrderStatus;
import com.example.bookido.order.domain.Recipient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;

import static com.example.bookido.order.application.port.ManipulateOrderUseCase.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderServiceTest {
    @Autowired
    BookJpaRepository bookRepository;

    @Autowired
    ManipulateOrderService service;

    @Autowired
    CatalogUseCase catalogUseCase;

    @Autowired
    QueryOrderUseCase queryOrderService;

    @Test
    public void userCanPlaceOrder() {
        //Given
        Book effectiveJava = givenEffectiveJava(50L);
        Book javaConcurrency = givenJavaConcurrency(50L);
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(effectiveJava.getId(), 15))
                .item(new OrderItemCommand(javaConcurrency.getId(), 10))
                .build();
        //When
        PlaceOrderResponse response = service.placeOrder(command);
        //Then
        assertTrue(response.isSuccess());
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(40L, availableCopiesOf(javaConcurrency));

    }

    @Test
    public void userCantOrderMoreBooksThanAvailable() {
        //Given
        Book effectiveJava = givenEffectiveJava(5L);
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(effectiveJava.getId(), 10))
                .build();
        //When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.placeOrder(command));
        //Then
        assertTrue(exception.getMessage().contains("Too many copies of book " + effectiveJava.getId() + " requested"));
    }

    @Test
    public void userCanRevokeOrder() {
        //Given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "marek@example.com";
        Long orderId = placeOrder(effectiveJava.getId(), 15, recipient);
        assertEquals(35, availableCopiesOf(effectiveJava));

        //When
        //TODO-Karolina: fix on security module
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, recipient);
        service.updateOrderStatus(command);

        //Then
        assertEquals(50L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.CANCELED, queryOrderService.findById(orderId).get().getStatus());
    }

    @Test
    public void userCannotRevokeOtherUsersOrder() {
        //Given
        Book effectiveJava = givenEffectiveJava(50L);
        String adam = "adam@example.org";
        Long orderId = placeOrder(effectiveJava.getId(), 15, adam);
        assertEquals(35, availableCopiesOf(effectiveJava));

        //When
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, "marek@example.org");
        service.updateOrderStatus(command);

        //Then
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.NEW, queryOrderService.findById(orderId).get().getStatus());
    }

    @Disabled("homework")
    public void userCannotREvokePaidOrder() {
        // user nie moe wycofac juz oplaconego zamowienia
    }

    @Disabled("homework")
    public void userCannotRevokeShippedOrder() {
        // user nie moze wycofac juz wyslanego zamowienia
    }

    @Disabled("homework")
    public void userCannotOrderNoExistingBooks() {
        // user nie moze zamowic nieistniejacych ksiazek
    }

    @Disabled("homework")
    public void userCannotOrderNegativeNumberOfBooks() {
        // user nie moze zamowic ujemnej liczby ksiazek
    }

    private Long placeOrder(Long bookId, int copies, String recipient) {
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient(recipient))
                .item(new OrderItemCommand(bookId, copies))
                .build();
        return service.placeOrder(command).getRight();
    }

    private Long placeOrder(Long bookId, int copies) {
        return placeOrder(bookId, copies, "john@example.org");
    }

    private Recipient recipient() {
        return recipient("john@example.org");
    }

    private Recipient recipient(String email) {
        return Recipient.builder().email(email).build();
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