package com.example.bookido;
;
import com.example.bookido.catalog.application.port.CatalogUseCase;
import com.example.bookido.catalog.application.port.CatalogUseCase.UpdateBookCommand;
import com.example.bookido.catalog.application.port.CatalogUseCase.UpdateBookResponse;
import com.example.bookido.catalog.domain.Book;
import com.example.bookido.order.applocation.port.PlaceOrderUseCase;
import com.example.bookido.order.applocation.port.PlaceOrderUseCase.PlaceOrderCommand;
import com.example.bookido.order.applocation.port.PlaceOrderUseCase.PlaceOrderResponse;
import com.example.bookido.order.applocation.port.QueryOrderUseCase;
import com.example.bookido.order.domain.OrderItem;
import com.example.bookido.order.domain.Recipient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ApplicationStartup implements CommandLineRunner {

    private final CatalogUseCase catalog;
    private final PlaceOrderUseCase placeOrder;
    private final QueryOrderUseCase queryOrder;
    private final String title;
    private final Long limit;

    public ApplicationStartup(
            CatalogUseCase catalog,
            PlaceOrderUseCase placeOrder,
            QueryOrderUseCase queryOrder,
            @Value("Harry") String title,
            @Value("1") Long limit
    ) {
        this.catalog = catalog;
        this.placeOrder = placeOrder;
        this.queryOrder = queryOrder;
        this.title = title;
        this.limit = limit;
    }

    @Override
    public void run(String... args) {
        initData();
        searchCatalog();
        placeOrder();
    }

    private void placeOrder() {
        // find harry potter
        Book harryPotter = catalog.findOneByTitle("Harry Potter")
                .orElseThrow(() -> new IllegalStateException("Cannot find a book"));

        // find wzorce projektowe
        Book wzorce = catalog.findOneByTitle("Wzorce Projektowe")
                .orElseThrow(() -> new IllegalStateException("Cannot find a book"));
        // create recepient
        Recipient recipient = Recipient
                .builder()
                .name("Jan Kowalski")
                .phone("123-456-789")
                .street("Armii Krajowej 31")
                .city("Krakow")
                .zipCode("30-150")
                .email("jan@example.org")
                .build();
        // place order command
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(null)
                .item(new OrderItem(harryPotter, 16))
                .item(new OrderItem(wzorce, 7))
                .build();

        PlaceOrderResponse response = placeOrder.placeOrder(command);
        System.out.println("Created ORDER with id: " + response.getOrderId());
        // list all orders
        queryOrder.findAll()
                .forEach(order -> {
                    System.out.println("GOT ORDER WITH TOTAL PRICE: " + order.totalPrice() + " DETAILS: " + order);
                });

    }

    private void searchCatalog() {
        findByTitle();
        findAndUpdate();
        findByTitle();
    }

    private void initData() {
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Granica", "Zofia Nałkowska", 1835, new BigDecimal("12.90")));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Hamlet", "William Szekspir", 1602, new BigDecimal("15.90")));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Chłopi", "Władysław Reymont", 1908, new BigDecimal("23.90")));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Pan Wołodyjowski", "Henryk Sienkiewicz", 1888, new BigDecimal("16.90")));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Kukuczka", "Dariusz Kortko", 2016, new BigDecimal("55.90")));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Cień Wiatru", "Carlos Luiz Zafón", 2005, new BigDecimal("26.90")));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Java", "Cay S. Horstmann", 2016, new BigDecimal("22.90")));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Wzorce Projektowe", "Eric Freeman", 2021, new BigDecimal("7.90")));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Harry Potter i Komnata Tajemnic", "J. K. Rowling", 1998, new BigDecimal("33.90")));
        catalog.addBook(new CatalogUseCase.CreateBookCommand("Ostatnie Zyczenie", "Andrzej Sapkowski", 2014, new BigDecimal("45.90")));
    }

    private void findByTitle() {
        List<Book> bookList = catalog.findByTitle(title);
        bookList.forEach(System.out::println);
    }

    private void findAndUpdate() {
        System.out.println("Updating book...");
        catalog.findOneByTitleAndAuthor("Harry", "J. K. Rowling")
                .ifPresent(book -> {
                    final UpdateBookCommand command = UpdateBookCommand
                            .builder()
                            .id(book.getId())
                            .title("Harry Potter i Komnata Tajemnic Xdd")
                            .build();
                    UpdateBookResponse response = catalog.updateBook(command);
                    System.out.println("Updating book result: " + response.isSuccess());
                });

    }
}