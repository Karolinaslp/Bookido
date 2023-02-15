package com.example.bookido.order.domain;

import lombok.Value;

@Value
public class UpdateStatusResult {
    OrderStatus newStatus;
    boolean isRevoked;

    static UpdateStatusResult ok(OrderStatus newStatus) {
        return new UpdateStatusResult(newStatus, false);
    }

    static UpdateStatusResult revoked(OrderStatus newStatus) {
        return new UpdateStatusResult(newStatus, true);
    }
}
