package com.example.bookido.order.application;

import com.example.bookido.order.application.price.OrderPrice;
import com.example.bookido.order.domain.OrderItem;
import com.example.bookido.order.domain.OrderStatus;
import com.example.bookido.order.domain.Recipient;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Value
public
class RichOrder {
    Long id;
    OrderStatus status;
    Set<OrderItem> items;
    Recipient recipient;
    LocalDateTime createdAt;
    OrderPrice orderPrice;
    BigDecimal finalPrice;

}
