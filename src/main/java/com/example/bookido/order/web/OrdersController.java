package com.example.bookido.order.web;


import com.example.bookido.order.application.RichOrder;
import com.example.bookido.order.application.port.ManipulateOrderUseCase;
import com.example.bookido.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import com.example.bookido.order.application.port.ManipulateOrderUseCase.UpdateStatusCommand;
import com.example.bookido.order.application.port.QueryOrderUseCase;
import com.example.bookido.order.domain.OrderStatus;
import com.example.bookido.web.CreatedURI;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
class OrdersController {
    private final ManipulateOrderUseCase manipulateOrder;
    private final QueryOrderUseCase queryOrder;

    @GetMapping
    public List<RichOrder> getOrders() {
        return queryOrder.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RichOrder> getOrderById(@PathVariable Long id) {
        return queryOrder.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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

    @PutMapping("/{id}/status")
    @ResponseStatus(ACCEPTED)
    public void updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        OrderStatus orderStatus = OrderStatus
                .parseString(status)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Unknown status: " + status));
        // TODO-Karolina: fix in security module
        UpdateStatusCommand command = new UpdateStatusCommand(id, orderStatus, null);
        manipulateOrder.updateOrderStatus(command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        manipulateOrder.deleteOrderById(id);
    }

}

