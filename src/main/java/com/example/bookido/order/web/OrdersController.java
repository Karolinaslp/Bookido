package com.example.bookido.order.web;


import com.example.bookido.order.application.RichOrder;
import com.example.bookido.order.application.port.ManipulateOrderUseCase;
import com.example.bookido.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import com.example.bookido.order.application.port.ManipulateOrderUseCase.UpdateStatusCommand;
import com.example.bookido.order.application.port.QueryOrderUseCase;
import com.example.bookido.order.domain.OrderStatus;
import com.example.bookido.security.UserSecurity;
import com.example.bookido.web.CreatedURI;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
class OrdersController {
    private final ManipulateOrderUseCase manipulateOrder;
    private final QueryOrderUseCase queryOrder;
    private final UserSecurity userSecurity;

    @Secured({"ROLE_ADMIN"})
    @GetMapping
    public List<RichOrder> getOrders() {
        return queryOrder.findAll();
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping("/{id}")
    public ResponseEntity<RichOrder> getOrderById(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        return queryOrder.findById(id)
                .filter(order -> userSecurity.isOwnerOrAdmin(order.getRecipient().getEmail(), user))
                .map(order -> authorize(order, user))
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<RichOrder> authorize(RichOrder order, UserDetails user) {
        if (userSecurity.isOwnerOrAdmin(order.getRecipient().getEmail(), user)) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.status(FORBIDDEN).build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createOrder(@RequestBody PlaceOrderCommand command) {
        return manipulateOrder
                .placeOrder(command)
                .handle(
                        orderId -> ResponseEntity.created(orderUri(orderId)).build(),
                        error -> ResponseEntity.badRequest().body(error)
                );
    }

    URI orderUri(Long orderId) {
        return new CreatedURI("/" + orderId).uri();
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @PatchMapping("/{id}/status")
    public ResponseEntity<Object> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> body, @AuthenticationPrincipal UserDetails user) {
        String status = body.get("status");
        OrderStatus orderStatus = OrderStatus
                .parseString(status)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Unknown status: " + status));
        UpdateStatusCommand command = new UpdateStatusCommand(id, orderStatus, user);
        return manipulateOrder.updateOrderStatus(command)
                .handle(
                        newStatus -> ResponseEntity.accepted().build(),
                        error -> ResponseEntity.status(error.getStatus()).build()
                );
    }

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        manipulateOrder.deleteOrderById(id);
    }

}

