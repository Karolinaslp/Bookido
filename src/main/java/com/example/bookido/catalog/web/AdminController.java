package com.example.bookido.catalog.web;

import com.example.bookido.catalog.application.port.CatalogUseCase;
import com.example.bookido.catalog.application.port.CatalogUseCase.CreateBookCommand;
import com.example.bookido.catalog.db.AuthorJpaRepository;
import com.example.bookido.catalog.domain.Author;
import com.example.bookido.catalog.domain.Book;
import com.example.bookido.order.applocation.port.ManipulateOrderUseCase;
import com.example.bookido.order.applocation.port.ManipulateOrderUseCase.OrderItemCommand;
import com.example.bookido.order.applocation.port.QueryOrderUseCase;
import com.example.bookido.order.domain.OrderItem;
import com.example.bookido.order.domain.Recipient;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Set;

import static com.example.bookido.order.applocation.port.ManipulateOrderUseCase.*;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    private final CatalogUseCase catalog;
    private final ManipulateOrderUseCase placeOrder;
    private final QueryOrderUseCase queryOrder;
    private final AuthorJpaRepository authorJpaRepository;

    @PostMapping("/data")
    public void initialize() {
        initData();
        placeOrder();
    }


    private void placeOrder() {

        Book effectiveJava = catalog.findOneByTitle("Effective Java")
                .orElseThrow(() -> new IllegalStateException("Cannot find a book"));

        Book puzzlers = catalog.findOneByTitle("Java Puzzlers")
                .orElseThrow(() -> new IllegalStateException("Cannot find a book"));

        Recipient recipient = Recipient
                .builder()
                .name("Jan Kowalski")
                .phone("123-456-789")
                .street("Armii Krajowej 31")
                .city("Krakow")
                .zipCode("30-150")
                .email("jan@example.org")
                .build();

            PlaceOrderCommand command = PlaceOrderCommand
                    .builder()
                    .recipient(recipient)
                    .item(new OrderItemCommand(effectiveJava.getId(), 16))
                    .item(new OrderItemCommand(puzzlers.getId(), 7))
                    .build();

        PlaceOrderResponse response = placeOrder.placeOrder(command);
        String result = response.handle(
                orderId -> "Creates ORDER with id: " + orderId,
                error -> "Failed to create order: " + error
        );
        // list all orders
        queryOrder.findAll()
                .forEach(order -> System.out.println("GOT ORDER WITH TOTAL PRICE: " + order.totalPrice() + " DETAILS: " + order));
    }

    private void initData() {
        Author joshua = new Author("Joshua", "Bloch");
        Author neal = new Author("Neal", "Gafter");
        authorJpaRepository.save(joshua);
        authorJpaRepository.save(neal);

        CreateBookCommand effectiveJava = new CreateBookCommand(
                "Effective Java",
                Set.of(joshua.getId()),
                2005,
                new BigDecimal("89.00"),
                50L
        );
        CreateBookCommand wzorceProjektowe = new CreateBookCommand(
                "Wzorce Projektowe", Set.of(),
                2021,
                new BigDecimal("109.00"),
                50L
        );

        CreateBookCommand java = new CreateBookCommand(
                "Java",
                Set.of(),
                2016,
                new BigDecimal("22.90"),
                50L
        );
        CreateBookCommand javaPrzewodnikDlaPoczatkujacych = new CreateBookCommand(
                "Java Przewodnik dla początkujących",
                Set.of(),
                2020,
                new BigDecimal("89.00"),
                50L
        );
        CreateBookCommand algorytmy = new CreateBookCommand(
                "algorytrmy",
                Set.of(),
                2016,
                new BigDecimal("67.00"),
                50L
        );
        CreateBookCommand javaPuzzlers = new CreateBookCommand(
                "Java Puzzlers",
                Set.of(joshua.getId(), neal.getId()),
                2018,
                new BigDecimal("99.00"),
                50L
        );

        catalog.addBook(effectiveJava);
        catalog.addBook(wzorceProjektowe);
        catalog.addBook(java);
        catalog.addBook(javaPrzewodnikDlaPoczatkujacych);
        catalog.addBook(algorytmy);
        catalog.addBook(javaPuzzlers);
    }
}
