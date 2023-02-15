package com.example.bookido.order.application.price;

import com.example.bookido.order.domain.Order;

import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal calculate(Order order);
}
