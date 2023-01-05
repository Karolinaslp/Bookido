package com.example.bookido.catalog.web;

import com.example.bookido.catalog.application.port.CatalogUseCase;
import com.example.bookido.catalog.db.AuthorJpaRepository;
import com.example.bookido.catalog.domain.Author;
import com.example.bookido.catalog.domain.Book;
import com.example.bookido.order.applocation.port.ManipulateOrderUseCase;
import com.example.bookido.order.applocation.port.QueryOrderUseCase;
import com.example.bookido.order.domain.OrderItem;
import com.example.bookido.order.domain.Recipient;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Set;

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

        if (true) {
            throw new IllegalStateException("POISON!");
        }
            ManipulateOrderUseCase.PlaceOrderCommand command = ManipulateOrderUseCase.PlaceOrderCommand
                    .builder()
                    .recipient(recipient)
                    .item(new OrderItem(effectiveJava.getId(), 16))
                    .item(new OrderItem(puzzlers.getId(), 7))
                    .build();

        ManipulateOrderUseCase.PlaceOrderResponse response = placeOrder.placeOrder(command);
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

        CatalogUseCase.CreateBookCommand effectiveJava = new CatalogUseCase.CreateBookCommand(
                "Effective Java",
                Set.of(joshua.getId()),
                2005,
                new BigDecimal("89.00")
        );
        CatalogUseCase.CreateBookCommand wzorceProjektowe = new CatalogUseCase.CreateBookCommand(
                "Wzorce Projektowe", Set.of(),
                2021,
                new BigDecimal("109.00")
        );

        CatalogUseCase.CreateBookCommand java = new CatalogUseCase.CreateBookCommand(
                "Java",
                Set.of(),
                2016,
                new BigDecimal("22.90")
        );
        CatalogUseCase.CreateBookCommand javaPrzewodnikDlaPoczatkujacych = new CatalogUseCase.CreateBookCommand(
                "Java Przewodnik dla początkujących",
                Set.of(),
                2020,
                new BigDecimal("89.00")
        );
        CatalogUseCase.CreateBookCommand algorytmy = new CatalogUseCase.CreateBookCommand(
                "algorytrmy",
                Set.of(),
                2016,
                new BigDecimal("67.00")
        );
        CatalogUseCase.CreateBookCommand javaPuzzlers = new CatalogUseCase.CreateBookCommand(
                "Java Puzzlers",
                Set.of(joshua.getId(), neal.getId()),
                2018,
                new BigDecimal("99.00")
        );

        catalog.addBook(effectiveJava);
        catalog.addBook(wzorceProjektowe);
        catalog.addBook(java);
        catalog.addBook(javaPrzewodnikDlaPoczatkujacych);
        catalog.addBook(algorytmy);
        catalog.addBook(javaPuzzlers);
    }
}
