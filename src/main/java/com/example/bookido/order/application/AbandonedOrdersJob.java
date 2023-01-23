package com.example.bookido.order.application;

import com.example.bookido.order.application.port.ManipulateOrderUseCase;
import com.example.bookido.order.db.OrderJpaRepository;
import com.example.bookido.order.domain.Order;
import com.example.bookido.order.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class AbandonedOrdersJob {
    private final OrderJpaRepository repository;
    private final ManipulateOrderUseCase orderUseCase;

    private final OrdersProperties properties;

    @Transactional
    @Scheduled(cron = "${app.orders.abandon-cron}")
    public void run() {
        Duration paymentPeriod = properties.getPaymentPeriod();
        // find abandoned orders
        LocalDateTime olderThan = LocalDateTime.now().minus(paymentPeriod);
        List<Order> orders = repository.findByStatusAndCreatedAtLessThanEqual(OrderStatus.NEW, olderThan);
        log.info("Found orders to be abandoned: " + orders.size());
        // update status as abandoned
        orders.forEach(order -> orderUseCase.updateOrderStatus(order.getId(), OrderStatus.ABANDONED));
    }
}