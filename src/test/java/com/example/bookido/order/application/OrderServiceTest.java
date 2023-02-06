package com.example.bookido.order.application;

import com.example.bookido.catalog.application.port.CatalogUseCase;
import com.example.bookido.catalog.db.BookJpaRepository;
import com.example.bookido.catalog.domain.Book;
import com.example.bookido.order.application.port.QueryOrderUseCase;
import com.example.bookido.order.domain.Delivery;
import com.example.bookido.order.domain.OrderStatus;
import com.example.bookido.order.domain.Recipient;
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

    @Test
    public void userCannotRevokePaidOrder() {
        // Given
        Book effectiveJava = givenEffectiveJava(50L);
        String adam = "adam@example.org";
        Long orderId = placeOrder(effectiveJava.getId(), 15, adam);

        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.PAID, adam);
        service.updateOrderStatus(command);

        // When
        UpdateStatusCommand cancelCommand = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, adam);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.updateOrderStatus(cancelCommand));

        // Then
        assertEquals(35, availableCopiesOf(effectiveJava));
        assertTrue(exception.getMessage().contains("Unable to mark"));
    }

    @Test
    public void userCannotRevokeShippedOrder() {
        // Given
        Book effectiveJava = givenEffectiveJava(50L);
        String adam = "adam@example.org";
        Long orderId = placeOrder(effectiveJava.getId(), 15, adam);

        UpdateStatusCommand paidCommand = new UpdateStatusCommand(orderId, OrderStatus.PAID, adam);
        service.updateOrderStatus(paidCommand);

        UpdateStatusCommand shippedCommand = new UpdateStatusCommand(orderId, OrderStatus.SHIPPED, adam);
        service.updateOrderStatus(shippedCommand);

        // When
        UpdateStatusCommand canceledCommand = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, adam);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.updateOrderStatus(canceledCommand));

        //Then
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertTrue(exception.getMessage().contains("Unable to mark"));
    }

    @Test
    public void userCannotOrderNoExistingBooks() {
        // Given
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(-3L, 10))
                .build();

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.placeOrder(command));

        //Then
        assertEquals("The book with id: " + command.getItems().get(0).getBookId() + " does not exist in our repository.", exception.getMessage());
    }

    @Test
    public void userCannotOrderNegativeNumberOfBooks() {
        // Given
        Book effectiveJava = givenEffectiveJava(5L);
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(effectiveJava.getId(), -5))
                .build();

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.placeOrder(command));

        // Then
        assertEquals("Quantity cannot be negative.", exception.getMessage());
    }

    @Test
    public void adminCanRevokeOtherUsersOrder() {
        //Given
        Book effectiveJava = givenEffectiveJava(50L);
        String marek = "marek@example.org";
        Long orderId = placeOrder(effectiveJava.getId(), 15, marek);
        assertEquals(35, availableCopiesOf(effectiveJava));

        //When
        String admin = "admin@example.org";
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, admin);
        service.updateOrderStatus(command);

        //Then
        assertEquals(50L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.CANCELED, queryOrderService.findById(orderId).get().getStatus());
    }

    @Test
    public void adminCanMarkOrderAsPaid() {
        //Given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "marek@example.com";
        Long orderId = placeOrder(effectiveJava.getId(), 15, recipient);
        assertEquals(35, availableCopiesOf(effectiveJava));

        //When
        String admin = "admin@example.org";
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.PAID, admin);
        service.updateOrderStatus(command);

        //Then
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.PAID, queryOrderService.findById(orderId).get().getStatus());
    }

    @Test
    public void shippingCostsAreAddedToTotalOrderPrice() {
        // Given
        Book book = givenBook(50L, "49.90");

        // When
        Long orderId = placeOrder(book.getId(), 1);

        // Then
        assertEquals("59.80", orderOf(orderId).getFinalPrice().toPlainString());
    }

    @Test
    public void shippingCostsAreDiscountedOver100zl() {
        // Given
        Book book = givenBook(50L, "49.90");

        // When
        Long orderId = placeOrder(book.getId(), 3);

        //Then
        RichOrder order = orderOf(orderId);
        assertEquals("149.70", order.getFinalPrice().toPlainString());
        assertEquals("149.70", order.getOrderPrice().getItemPrice().toPlainString());
    }

    @Test
    public void cheapestBookIsHalfPriceWhenTotalOver200zl() {
        // Given
        Book book = givenBook(50L, "49.90");

        // When
        Long orderId = placeOrder(book.getId(), 5);

        // Then
        RichOrder order = orderOf(orderId);
        assertEquals("224.55", order.getFinalPrice().toPlainString());
    }

    @Test
    public void cheapestBookIsFreeWhenTotalOver400zl() {
        // Given
        Book book = givenBook(50L, "49.90");

        // When
        Long orderId = placeOrder(book.getId(), 10);

        // Then
        RichOrder order = orderOf(orderId);
        assertEquals("449.10", order.getFinalPrice().toPlainString());
    }

    private Long placeOrder(Long bookId, int copies, String recipient) {
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient(recipient))
                .item(new OrderItemCommand(bookId, copies))
                .delivery(Delivery.COURIER)
                .build();
        return service.placeOrder(command).getRight();
    }

    private RichOrder orderOf(Long orderId) {
        return queryOrderService.findById(orderId).get();
    }

    private Book givenBook(Long available, String price) {
        return bookRepository.save(new Book("Java Concurrency in Practice", 2006, new BigDecimal(price), available));
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