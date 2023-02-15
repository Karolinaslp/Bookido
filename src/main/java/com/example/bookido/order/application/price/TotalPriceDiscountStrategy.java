package com.example.bookido.order.application.price;

import com.example.bookido.order.domain.Order;
import com.example.bookido.order.domain.OrderItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

public class TotalPriceDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculate(Order order) {
        if (isGreaterOrEqual(order, 400)) {
            // cheapest book for free
            return lowestBookPrice(order.getItems());
        } else if (isGreaterOrEqual(order, 200)) {
            // cheapest book half price
            BigDecimal lowestPrice = lowestBookPrice(order.getItems());
            return lowestPrice.divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal lowestBookPrice(Set<OrderItem> items) {
       return items.stream()
                .map(x -> x.getBook().getPrice())
                .sorted()
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    private static boolean isGreaterOrEqual(Order order, int value) {
        return order.getItemsPrice().compareTo(BigDecimal.valueOf(value)) >= 0;
    }
}
