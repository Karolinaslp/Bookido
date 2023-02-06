package com.example.bookido.order.application;

import com.example.bookido.catalog.db.BookJpaRepository;
import com.example.bookido.catalog.domain.Book;
import com.example.bookido.order.application.port.ManipulateOrderUseCase;
import com.example.bookido.order.db.OrderJpaRepository;
import com.example.bookido.order.db.RecipientJpaRepository;
import com.example.bookido.order.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ManipulateOrderService implements ManipulateOrderUseCase {
    private final RecipientJpaRepository recipientJpaRepository;
    private final OrderJpaRepository repository;
    private final BookJpaRepository bookJpaRepository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
        Set<OrderItem> items = command
                .getItems()
                .stream()
                .map(this::toOrderItem)
                .collect(Collectors.toSet());

        Order order = Order
                .builder()
                .recipient(getOrCreateRecipient(command.getRecipient()))
                .delivery(command.getDelivery())
                .items(items)
                .build();
        Order savedOrder = repository.save(order);
        bookJpaRepository.saveAll(reduceBooks(items));
        return PlaceOrderResponse.success(savedOrder.getId());
    }

    private Recipient getOrCreateRecipient(Recipient recipient) {
        return recipientJpaRepository
                .findByEmailIgnoreCase(recipient.getEmail())
                .orElse(recipient);

    }

    private Set<Book> reduceBooks(Set<OrderItem> items) {
        return items
                .stream()
                .map(item -> {
                    Book book = item.getBook();
                    book.setAvailable(book.getAvailable() - item.getQuantity());
                    return book;
                })
                .collect(Collectors.toSet());
    }

    private OrderItem toOrderItem(OrderItemCommand command) {
        Book book = getOneBook(command);
        int quantity = command.getQuantity();
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative.");
        if (book.getAvailable() >= quantity) {
            return new OrderItem(book, quantity);
        }
        throw new IllegalArgumentException("Too many copies of book " + book.getId() + " requested: " + quantity + " of " + book.getAvailable() + " available ");
    }

    private Book getOneBook(OrderItemCommand command) {
        Optional<Book> bookOptional = bookJpaRepository.findById(command.getBookId());
        if (bookOptional.isEmpty()) {
            throw new IllegalArgumentException("The book with id: " + command.getBookId() + " does not exist in our repository.");
        }
        return bookOptional.get();
    }

    @Override
    public void deleteOrderById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public UpdateStatusResponse updateOrderStatus(UpdateStatusCommand command) {
        return repository.findById(command.getOrderId())
                .map(order -> {
                    if (!hasAccess(command, order)) {
                        return UpdateStatusResponse.failure("Unauthorized");
                    }
                    UpdateStatusResult result = order.updateStatus(command.getStatus());
                    if (result.isRevoked()) {
                        bookJpaRepository.saveAll(revokeBooks(order.getItems()));
                    }
                    repository.save(order);
                    return UpdateStatusResponse.success(order.getStatus());
                })
                .orElse(UpdateStatusResponse.failure("Order not found"));
    }

    private static boolean hasAccess(UpdateStatusCommand command, Order order) {
        String email = command.getEmail();
        return command.getEmail().equalsIgnoreCase(order.getRecipient().getEmail()) ||
                email.equalsIgnoreCase("admin@example.org");

    }

    private Set<Book> revokeBooks(Set<OrderItem> items) {
        return items
                .stream()
                .map(item -> {
                    Book book = item.getBook();
                    book.setAvailable(book.getAvailable() + item.getQuantity());
                    return book;
                })
                .collect(Collectors.toSet());
    }
}
